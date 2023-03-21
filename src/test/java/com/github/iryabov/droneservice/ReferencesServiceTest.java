package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.model.MedicationForm;
import com.github.iryabov.droneservice.service.ReferencesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
        assertThat(service.getAllMedications().size(), is(0));
    }
}
