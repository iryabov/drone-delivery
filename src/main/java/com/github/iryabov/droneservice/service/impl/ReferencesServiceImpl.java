package com.github.iryabov.droneservice.service.impl;

import com.github.iryabov.droneservice.entity.Medication;
import com.github.iryabov.droneservice.mapper.ReferencesMapper;
import com.github.iryabov.droneservice.model.MedicationForm;
import com.github.iryabov.droneservice.repository.MedicationRepository;
import com.github.iryabov.droneservice.service.ReferencesService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@AllArgsConstructor
public class ReferencesServiceImpl implements ReferencesService {
    private MedicationRepository medicationRepo;
    private ReferencesMapper mapper;

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
        Medication entity = medicationRepo.save(mapper.toEntity(null, form));
        return entity.getId();
    }

    @Override
    public void updateMedication(int id, MedicationForm form) {
        medicationRepo.save(mapper.toEntity(id, form));
    }

    @Override
    public void deleteMedication(int id) {
        medicationRepo.deleteById(id);
    }
}
