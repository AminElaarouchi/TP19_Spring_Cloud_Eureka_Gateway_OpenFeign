package com.example.tp19.voiture;

import feign.FeignException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VoitureService {
    private final VoitureRepository repository;
    private final ClientService clients;

    public VoitureService(VoitureRepository repository, ClientService clients) {
        this.repository = repository;
        this.clients = clients;
    }

    public List<Voiture> findAll() {
        return repository.findAll().stream().map(this::withClient).toList();
    }

    public Voiture findById(Long id) {
        return withClient(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voiture introuvable: " + id)));
    }

    public List<Voiture> findByClient(Long clientId) {
        requireClient(clientId);
        return repository.findByClientId(clientId).stream().map(this::withClient).toList();
    }

    public Voiture create(Long clientId, Voiture input) {
        ClientDto client = requireClient(clientId);
        Voiture saved = repository.save(new Voiture(null, input.getMarque(), input.getMatricule(),
                input.getModel(), clientId));
        saved.setClient(client);
        return saved;
    }

    public Voiture update(Long id, Voiture changes) {
        Voiture existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voiture introuvable: " + id));
        if (changes.getMarque() != null) existing.setMarque(changes.getMarque());
        if (changes.getMatricule() != null) existing.setMatricule(changes.getMatricule());
        if (changes.getModel() != null) existing.setModel(changes.getModel());
        return withClient(repository.save(existing));
    }

    public void delete(Long id) {
        Voiture existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voiture introuvable: " + id));
        repository.delete(existing);
    }

    private Voiture withClient(Voiture voiture) {
        voiture.setClient(requireClient(voiture.getClientId()));
        return voiture;
    }

    private ClientDto requireClient(Long id) {
        try {
            return clients.clientById(id);
        } catch (FeignException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable: " + id, ex);
        }
    }
}
