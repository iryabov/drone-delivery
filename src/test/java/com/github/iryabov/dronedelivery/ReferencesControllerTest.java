package com.github.iryabov.dronedelivery;

import com.github.iryabov.dronedelivery.model.MedicationForm;
import com.github.iryabov.dronedelivery.model.ResponseError;
import com.github.iryabov.dronedelivery.service.ReferencesService;
import com.github.iryabov.dronedelivery.model.ResponseId;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReferencesControllerTest {
    @Autowired
    private WebTestClient client;
    @MockBean
    private ReferencesService service;
    @Autowired
    private Validator validator;

    @Test
    void create() {
        when(service.createMedication(any())).thenReturn(1);
        MedicationForm form = MedicationForm.builder().code("CODE").name("Medication").weight(0.1).build();
        client.post().uri("/api/references/medications")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(form), MedicationForm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseId.class).isEqualTo(new ResponseId<>(1));
    }

    @Test
    void update() {
        MedicationForm form = MedicationForm.builder().code("CODE").name("Medication").weight(0.1).build();
        client.put().uri("/api/references/medications/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(form), MedicationForm.class)
                .exchange()
                .expectStatus().isOk();
        verify(service).updateMedication(eq(1), any());
    }

    @Test
    void delete() {
        client.delete().uri("/api/references/medications/1")
                .exchange()
                .expectStatus().isOk();
        verify(service).deleteMedication(1);
    }

    @Test
    void read() {
        var result = MedicationForm.builder().id(1).code("CODE").name("Medication").weight(0.1).build();
        //get all
        when(service.getAllMedications(null, null)).thenReturn(List.of(result));
        client.get().uri("/api/references/medications")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MedicationForm.class)
                .contains(result);

        //get by id
        when(service.getOneMedication(1)).thenReturn(result);
        client.get().uri("/api/references/medications/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MedicationForm.class)
                .contains(result);
    }


    @Test
    void constraintValidations() {
        MedicationForm form = MedicationForm.builder().code("IncorrectCode$").name("IncorrectName%").weight(1.0).build();
        var violations = validator.validate(form);

        when(service.createMedication(form)).thenThrow(new ConstraintViolationException(violations));
        var result = client.post().uri("/api/references/medications")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(form), MedicationForm.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ResponseError.class)
                .returnResult();
        assertThat(result.getStatus(), is(HttpStatusCode.valueOf(400)));
        assertThat(result.getResponseBody(), notNullValue());
        assertThat(result.getResponseBody().getMessage(), not(emptyString()));
        assertThat(result.getResponseBody().getErrors().size(), is(2));
        assertThat(result.getResponseBody().getErrors().stream().map(ResponseError.Field::getField).collect(toList()), hasItems("name", "code"));
    }


}
