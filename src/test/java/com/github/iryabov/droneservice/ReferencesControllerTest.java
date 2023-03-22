package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.model.MedicationForm;
import com.github.iryabov.droneservice.service.ReferencesService;
import com.github.iryabov.droneservice.web.ResponseId;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReferencesControllerTest {
    @Autowired
    private WebTestClient client;
    @MockBean
    private ReferencesService service;

    @Test
    void create() {
        when(service.createMedication(any())).thenReturn(1);
        MedicationForm form = MedicationForm.builder().code("CODE").name("Medication").weight(0.1).build();
        client.post().uri("/references/medications")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(form), MedicationForm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseId.class).isEqualTo(new ResponseId<>(1));
    }

    @Test
    void update() {
        MedicationForm form = MedicationForm.builder().code("CODE").name("Medication").weight(0.1).build();
        client.put().uri("/references/medications/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(form), MedicationForm.class)
                .exchange()
                .expectStatus().isOk();
        verify(service).updateMedication(eq(1), any());
    }

    @Test
    void delete() {
        client.delete().uri("/references/medications/1")
                .exchange()
                .expectStatus().isOk();
        verify(service).deleteMedication(1);
    }

    @Test
    void read() {
        var result = MedicationForm.builder().id(1).code("CODE").name("Medication").weight(0.1).build();
        //get all
        when(service.getAllMedications()).thenReturn(List.of(result));
        client.get().uri("/references/medications")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MedicationForm.class)
                .contains(result);

        //get by id
        when(service.getOneMedication(1)).thenReturn(result);
        client.get().uri("/references/medications/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MedicationForm.class)
                .contains(result);
    }


}
