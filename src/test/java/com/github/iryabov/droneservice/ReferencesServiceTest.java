package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.model.MedicationForm;
import com.github.iryabov.droneservice.service.ReferencesService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ReferencesServiceTest {
    @Autowired
    private ReferencesService service;

    @Test
    void crudMedication() {
        //Creating medicine
        int medicineId = service.createMedication(MedicationForm.builder()
                .name("Penicillins")
                .code("PEN")
                .weight(0.05).build());
        assertThat(medicineId, greaterThan(0));
        assertThat(service.getAllMedications().size(), greaterThan(0));

        //Reading medicine
        var medication = service.getOneMedication(medicineId);
        assertThat(medication.getName(), is("Penicillins"));
        assertThat(medication.getCode(), is("PEN"));
        assertThat(medication.getWeight(), is(0.05));

        //Updating medicine
        medication.setWeight(0.1);
        service.updateMedication(medicineId, medication);
        assertThat(service.getOneMedication(medicineId).getWeight(), is(0.1));

        //Deleting medicine
        service.deleteMedication(medicineId);
        assertThat(service.getAllMedications().stream().map(MedicationForm::getCode).collect(toList()), not(hasItem("PEN")));
    }

    @Test
    void validationsMedication() {
        //Not valid, because the name contains special symbols
        assertThrows(ConstraintViolationException.class, () -> {
            service.createMedication(MedicationForm.builder()
                    .name("Inc$orr%ect-123_")
                    .code("CORRECT_123")
                    .weight(0.1).build());
        });

        //Not valid, because the code contains lowercase letters and -
        assertThrows(ConstraintViolationException.class, () -> {
            service.createMedication(MedicationForm.builder()
                    .name("Correct-123_")
                    .code("Incorrect-123")
                    .weight(0.1).build());
        });

        //Not valid, because the weight less than 0
        assertThrows(ConstraintViolationException.class, () -> {
            service.createMedication(MedicationForm.builder()
                    .name("Correct-123_")
                    .code("CORRECT_123")
                    .weight(-0.1).build());
        });

        //Not valid, because also mustn't update with the incorrect input
        assertThrows(ConstraintViolationException.class, () -> {
            service.updateMedication(1, MedicationForm.builder()
                    .name("Inc$orr%ect-123_")
                    .code("Incorrect-123")
                    .weight(-0.1).build());
        });

        //Valid, because all input are correct
        assertDoesNotThrow(() -> {
            service.createMedication(MedicationForm.builder()
                    .name("Correct-123_")
                    .code("CORRECT_123")
                    .weight(0.1).build());
        });
    }
}
