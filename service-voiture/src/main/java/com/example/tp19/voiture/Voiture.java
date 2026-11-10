package com.example.tp19.voiture;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Voiture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String marque;
    private String matricule;
    private String model;
    private Long clientId;

    // Le client appartient au service Client ; seule sa référence est persistée ici.
    @Transient
    private ClientDto client;

    protected Voiture() {
    }

    public Voiture(Long id, String marque, String matricule, String model, Long clientId) {
        this.id = id;
        this.marque = marque;
        this.matricule = matricule;
        this.model = model;
        this.clientId = clientId;
    }

    public Long getId() { return id; }
    public String getMarque() { return marque; }
    public String getMatricule() { return matricule; }
    public String getModel() { return model; }
    public Long getClientId() { return clientId; }
    public ClientDto getClient() { return client; }
    public void setMarque(String marque) { this.marque = marque; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public void setModel(String model) { this.model = model; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public void setClient(ClientDto client) { this.client = client; }
}
