package com.github.iryabov.droneservice.mapper;

import com.github.iryabov.droneservice.entity.Medication;
import com.github.iryabov.droneservice.model.MedicationForm;
import org.springframework.stereotype.Component;

@Component
public class ReferencesMapper {
    public MedicationForm toMedicationForm(Medication entity) {
        MedicationForm form = MedicationForm.builder().build();
        form.setName(entity.getName());
        form.setCode(entity.getCode());
        form.setWeight(entity.getWeight());
        return form;
    }

    public Medication toEntity(Integer id, MedicationForm form) {
        Medication entity = new Medication();
        entity.setId(id);
        entity.setName(form.getName());
        entity.setCode(form.getCode());
        entity.setWeight(form.getWeight());
        return entity;
    }
}
