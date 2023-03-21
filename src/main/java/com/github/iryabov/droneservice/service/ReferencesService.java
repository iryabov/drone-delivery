package com.github.iryabov.droneservice.service;

import com.github.iryabov.droneservice.model.MedicationForm;

import java.util.List;

public interface ReferencesService {
    List<MedicationForm> getAllMedications();
    MedicationForm getOneMedication(int id);
    int createMedication(MedicationForm form);
    void updateMedication(int id, MedicationForm form);
    void deleteMedication(int id);
}
