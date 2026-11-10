package com.example.tp19.client;

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
public class ClientController {
    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @GetMapping("/clients")
    public List<Client> findAll() { return service.findAll(); }

    @GetMapping({"/client/{id}", "/clients/{id}"})
    public Client findById(@PathVariable Long id) { return service.findById(id); }

    @PostMapping("/clients")
    public ResponseEntity<Client> create(@RequestBody Client client) {
        Client saved = service.create(client);
        return ResponseEntity.created(URI.create("/clients/" + saved.getId())).body(saved);
    }

    @PutMapping("/clients/{id}")
    public Client update(@PathVariable Long id, @RequestBody Client changes) {
        return service.update(id, changes);
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
