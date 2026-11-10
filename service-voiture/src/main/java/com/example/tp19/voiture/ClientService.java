package com.example.tp19.voiture;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SERVICE-CLIENT")
public interface ClientService {
    @GetMapping("/clients/{id}")
    ClientDto clientById(@PathVariable("id") Long id);
}
