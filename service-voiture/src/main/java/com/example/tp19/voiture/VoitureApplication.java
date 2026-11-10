package com.example.tp19.voiture;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class VoitureApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoitureApplication.class, args);
    }

    @Bean
    CommandLineRunner seedVoitures(VoitureRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Voiture(null, "Toyota", "A 25 333", "Corolla", 1L));
                repository.save(new Voiture(null, "Renault", "B 6 3456", "Megane", 1L));
                repository.save(new Voiture(null, "Peugeot", "A 55 4444", "301", 2L));
            }
        };
    }
}
