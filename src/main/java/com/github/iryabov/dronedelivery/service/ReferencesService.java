package com.github.iryabov.dronedelivery.service;

import com.github.iryabov.dronedelivery.model.MedicationForm;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * References maintenance service
 */
public interface ReferencesService {
    /**
     * Get list of medications
     * @param page Page number (0 by default)
     * @param size Page size (10 by default)
     * @return List of medications
     */
    List<MedicationForm> getAllMedications(@Nullable Integer page, @Nullable Integer size);

    /**
     * Get one of medication
     * @param id Medication identifier
     * @return Medication form
     */
    MedicationForm getOneMedication(int id);

    /**
     * Create a medication
     * @param form Medication form
     * @return Identifier
     */
    int createMedication(MedicationForm form);

    /**
     * Update a medication
     * @param id Medication identifier
     * @param form Medication form for update
     */
    void updateMedication(int id, MedicationForm form);

    /**
     * Delete a medication
     * @param id Medication identifier
     */
    void deleteMedication(int id);
}
