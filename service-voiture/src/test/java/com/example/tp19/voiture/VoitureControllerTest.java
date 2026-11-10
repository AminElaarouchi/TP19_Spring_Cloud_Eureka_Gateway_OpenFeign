package com.example.tp19.voiture;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class VoitureControllerTest {
    @Autowired
    MockMvc mvc;

    @MockitoBean
    ClientService clients;

    @Test
    void voitureIsEnrichedWithFeignClientData() throws Exception {
        when(clients.clientById(1L)).thenReturn(new ClientDto(1L, "Rabab SELIMANI", 23f));

        mvc.perform(get("/voitures/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marque").value("Toyota"))
                .andExpect(jsonPath("$.client.nom").value("Rabab SELIMANI"));
        mvc.perform(get("/voitures/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/voitures/999"))
                .andExpect(status().isNotFound());
    }
}
