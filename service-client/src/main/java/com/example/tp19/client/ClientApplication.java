package com.example.tp19.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    CommandLineRunner seedClients(ClientRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Client(null, "Rabab SELIMANI", 23f));
                repository.save(new Client(null, "Amal RAMI", 22f));
                repository.save(new Client(null, "Samir SAFI", 22f));
            }
        };
    }
}
