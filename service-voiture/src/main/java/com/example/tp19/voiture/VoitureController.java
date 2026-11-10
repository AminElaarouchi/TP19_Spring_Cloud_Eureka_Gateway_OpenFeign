package com.example.tp19.voiture;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoitureController {
    private final VoitureService service;

    public VoitureController(VoitureService service) {
        this.service = service;
    }

    @GetMapping("/voitures")
    public List<Voiture> findAll() { return service.findAll(); }

    @GetMapping("/voitures/{id}")
    public Voiture findById(@PathVariable Long id) { return service.findById(id); }

    @GetMapping("/voitures/client/{id}")
    public List<Voiture> findByClient(@PathVariable Long id) { return service.findByClient(id); }

    @PostMapping("/voitures/{clientId}")
    public ResponseEntity<Voiture> create(@PathVariable Long clientId, @RequestBody Voiture voiture) {
        Voiture saved = service.create(clientId, voiture);
        return ResponseEntity.created(URI.create("/voitures/" + saved.getId())).body(saved);
    }

    @PutMapping("/voitures/{id}")
    public Voiture update(@PathVariable Long id, @RequestBody Voiture changes) {
        return service.update(id, changes);
    }

    @DeleteMapping("/voitures/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
