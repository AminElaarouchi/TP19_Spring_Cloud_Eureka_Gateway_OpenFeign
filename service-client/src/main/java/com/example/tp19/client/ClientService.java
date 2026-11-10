package com.example.tp19.client;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClientService {
    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<Client> findAll() {
        return repository.findAll();
    }

    public Client findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable: " + id));
    }

    public Client create(Client client) {
        return repository.save(new Client(null, client.getNom(), client.getAge()));
    }

    public Client update(Long id, Client changes) {
        Client client = findById(id);
        client.setNom(changes.getNom());
        client.setAge(changes.getAge());
        return repository.save(client);
    }

    public void delete(Long id) {
        Client client = findById(id);
        repository.delete(client);
    }
}
