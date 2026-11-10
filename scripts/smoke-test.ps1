param([int]$TimeoutSeconds = 180)

$ErrorActionPreference = 'Stop'
$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$logDir = Join-Path $projectRoot 'target\smoke-test'
New-Item -ItemType Directory -Path $logDir -Force | Out-Null

foreach ($port in @(8761, 8088, 8089, 8888)) {
    if (Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue) {
        throw "Le port $port est déjà utilisé. Arrêtez le service concerné avant le test."
    }
}

$started = @()

function Start-LabService([string]$module) {
    $jar = Join-Path $projectRoot "$module\target\$module-1.0-SNAPSHOT.jar"
    if (-not (Test-Path -LiteralPath $jar)) {
        throw "Archive absente : $jar. Exécutez d'abord mvn package."
    }
    $process = Start-Process -FilePath (Get-Command java).Source `
        -ArgumentList @('-jar', ('"{0}"' -f $jar)) -PassThru -WindowStyle Hidden `
        -RedirectStandardOutput (Join-Path $logDir "$module.out.log") `
        -RedirectStandardError (Join-Path $logDir "$module.err.log")
    $script:started += $process
    Write-Host "Démarrage de $module (PID $($process.Id))"
}

function Wait-Endpoint([string]$url) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            return Invoke-RestMethod -Uri $url -TimeoutSec 5
        } catch {
            Start-Sleep -Seconds 2
        }
    }
    throw "Délai dépassé pour $url. Consultez les journaux dans $logDir."
}

function Wait-ArrayCount([string]$url, [int]$expected) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $content = (Invoke-WebRequest -Uri $url -TimeoutSec 5).Content
            $items = ConvertFrom-Json -InputObject $content -NoEnumerate
            if ($items.Count -eq $expected) { return }
        } catch {
            # Le service ou son enregistrement Eureka peut encore démarrer.
        }
        Start-Sleep -Seconds 2
    }
    throw "$expected éléments attendus pour $url. Consultez les journaux dans $logDir."
}

try {
    Start-LabService 'eureka-server'
    Wait-Endpoint 'http://localhost:8761/' | Out-Null

    Start-LabService 'service-client'
    Wait-ArrayCount 'http://localhost:8088/clients' 3

    Start-LabService 'service-voiture'
    Wait-Endpoint 'http://localhost:8089/actuator/health' | Out-Null

    Start-LabService 'api-gateway'
    Wait-Endpoint 'http://localhost:8888/actuator/health' | Out-Null
    Wait-ArrayCount 'http://localhost:8888/clients' 3

    $voiture = Wait-Endpoint 'http://localhost:8888/voitures/1'
    if ($voiture.marque -ne 'Toyota' -or $voiture.client.nom -ne 'Rabab SELIMANI') {
        throw 'La voiture 1 ne contient pas les données Client obtenues via OpenFeign.'
    }

    Wait-ArrayCount 'http://localhost:8888/SERVICE-CLIENT/clients' 3
    Wait-ArrayCount 'http://localhost:8888/SERVICE-VOITURE/voitures' 3

    $eurekaApps = (Invoke-WebRequest -Uri 'http://localhost:8761/eureka/apps' -TimeoutSec 5).Content
    if ($eurekaApps -notmatch 'SERVICE-CLIENT' -or $eurekaApps -notmatch 'SERVICE-VOITURE') {
        throw 'Eureka ne liste pas les deux microservices.'
    }

    $newVoiture = Invoke-RestMethod -Method Post -Uri 'http://localhost:8888/voitures/1' `
        -ContentType 'application/json' -Body '{"marque":"Honda","matricule":"TEST 19","model":"Civic"}'
    if ($newVoiture.clientId -ne 1 -or $newVoiture.client.nom -ne 'Rabab SELIMANI') {
        throw 'La création via Gateway et Feign a échoué.'
    }
    $updatedVoiture = Invoke-RestMethod -Method Put -Uri "http://localhost:8888/voitures/$($newVoiture.id)" `
        -ContentType 'application/json' -Body '{"model":"Civic test"}'
    if ($updatedVoiture.model -ne 'Civic test') { throw 'La modification via Gateway a échoué.' }
    Invoke-WebRequest -Method Delete -Uri "http://localhost:8888/voitures/$($newVoiture.id)" | Out-Null

    # La seconde configuration du TP dirige la Gateway vers des URLs fixes.
    Stop-Process -Id $started[-1].Id -Force
    $deadline = (Get-Date).AddSeconds(20)
    while ((Get-NetTCPConnection -LocalPort 8888 -State Listen -ErrorAction SilentlyContinue) -and (Get-Date) -lt $deadline) {
        Start-Sleep -Milliseconds 500
    }
    $gatewayJar = Join-Path $projectRoot 'api-gateway\target\api-gateway-1.0-SNAPSHOT.jar'
    $staticGateway = Start-Process -FilePath (Get-Command java).Source `
        -ArgumentList @('-jar', ('"{0}"' -f $gatewayJar), '--spring.profiles.active=static') `
        -PassThru -WindowStyle Hidden `
        -RedirectStandardOutput (Join-Path $logDir 'api-gateway-static.out.log') `
        -RedirectStandardError (Join-Path $logDir 'api-gateway-static.err.log')
    $started += $staticGateway
    Write-Host "Démarrage de api-gateway (profil static, PID $($staticGateway.Id))"
    Wait-ArrayCount 'http://localhost:8888/clients' 3
    $staticClient = Wait-Endpoint 'http://localhost:8888/client/1'
    if ($staticClient.nom -ne 'Rabab SELIMANI') { throw 'Route statique /client/1 incorrecte.' }
    Wait-ArrayCount 'http://localhost:8888/voitures' 3

    Write-Host 'OK : Eureka, 3 clients, 3 voitures, routes dynamiques et statiques, OpenFeign, POST/PUT/DELETE.'
} finally {
    foreach ($process in $started) {
        Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
    }
}
