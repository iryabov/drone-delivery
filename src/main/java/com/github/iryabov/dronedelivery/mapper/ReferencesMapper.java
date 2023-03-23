package com.github.iryabov.dronedelivery.mapper;

import com.github.iryabov.dronedelivery.entity.Image;
import com.github.iryabov.dronedelivery.entity.Medication;
import com.github.iryabov.dronedelivery.model.MedicationForm;
import org.springframework.stereotype.Component;

@Component
public class ReferencesMapper {
    public MedicationForm toMedicationForm(Medication entity) {
        MedicationForm form = MedicationForm.builder().build();
        form.setId(entity.getId());
        form.setName(entity.getName());
        form.setCode(entity.getCode());
        form.setWeight(entity.getWeight());
        if (entity.getImage() != null) {
            form.setImageId(entity.getImage().getId());
        }
        return form;
    }

    public Medication toEntity(Integer id, MedicationForm form) {
        Medication entity = new Medication();
        entity.setId(id);
        entity.setName(form.getName());
        entity.setCode(form.getCode());
        entity.setWeight(form.getWeight());
        if (form.getImageId() != null) {
            Image image = new Image();
            image.setId(form.getImageId());
            entity.setImage(image);
        }
        return entity;
    }
}
