package com.example.tp19.client;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private Float age;

    protected Client() {
    }

    public Client(Long id, String nom, Float age) {
        this.id = id;
        this.nom = nom;
        this.age = age;
    }

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public Float getAge() { return age; }
    public void setNom(String nom) { this.nom = nom; }
    public void setAge(Float age) { this.age = age; }
}
