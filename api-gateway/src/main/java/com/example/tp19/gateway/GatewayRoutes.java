package com.example.tp19.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class GatewayRoutes {
    @Bean
    @Profile("!static")
    RouteLocator eurekaRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("clients", r -> r.path("/clients", "/clients/**", "/client/**")
                        .uri("lb://SERVICE-CLIENT"))
                .route("voitures", r -> r.path("/voitures", "/voitures/**")
                        .uri("lb://SERVICE-VOITURE"))
                .route("clients-by-name", r -> r.path("/SERVICE-CLIENT/**")
                        .filters(f -> f.stripPrefix(1)).uri("lb://SERVICE-CLIENT"))
                .route("voitures-by-name", r -> r.path("/SERVICE-VOITURE/**")
                        .filters(f -> f.stripPrefix(1)).uri("lb://SERVICE-VOITURE"))
                .build();
    }

    @Bean
    @Profile("static")
    RouteLocator staticRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("clients-static", r -> r.path("/clients", "/clients/**", "/client/**")
                        .uri("http://localhost:8088"))
                .route("voitures-static", r -> r.path("/voitures", "/voitures/**")
                        .uri("http://localhost:8089"))
                .build();
    }
}
