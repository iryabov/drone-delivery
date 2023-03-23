package com.github.iryabov.dronedelivery.service.impl;

import com.github.iryabov.dronedelivery.entity.Medication;
import com.github.iryabov.dronedelivery.mapper.ReferencesMapper;
import com.github.iryabov.dronedelivery.model.MedicationForm;
import com.github.iryabov.dronedelivery.repository.MedicationRepository;
import com.github.iryabov.dronedelivery.service.ReferencesService;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.github.iryabov.dronedelivery.validation.ValidateUtil.validate;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
@AllArgsConstructor
public class ReferencesServiceImpl implements ReferencesService {
    private MedicationRepository medicationRepo;
    private ReferencesMapper mapper;
    private Validator validator;

    @Override
    public List<MedicationForm> getAllMedications() {
        return medicationRepo.findAll().stream().map(mapper::toMedicationForm).collect(toList());
    }

    @Override
    public MedicationForm getOneMedication(int id) {
        return mapper.toMedicationForm(medicationRepo.findById(id).orElseThrow());
    }

    @Override
    public int createMedication(MedicationForm form) {
        validate(validator, form);
        Medication entity = medicationRepo.save(mapper.toEntity(null, form));
        return entity.getId();
    }

    @Override
    public void updateMedication(int id, MedicationForm form) {
        validate(validator, form);
        medicationRepo.save(mapper.toEntity(id, form));
    }

    @Override
    public void deleteMedication(int id) {
        medicationRepo.deleteById(id);
    }

}
