package com.github.iryabov.dronedelivery.controller;

import com.github.iryabov.dronedelivery.model.MedicationForm;
import com.github.iryabov.dronedelivery.service.ReferencesService;
import com.github.iryabov.dronedelivery.model.ResponseId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * References services
 */
@RestController
@RequestMapping("/api/references")
@AllArgsConstructor
public class ReferencesController {
    private ReferencesService service;

    @Operation(summary = "Get a list of medications")
    @ApiResponse(responseCode = "200", description = "List of medications")
    @GetMapping("/medications")
    public List<MedicationForm> getAllMedications(@Parameter(description = "Page number (0 by default)") @RequestParam(name = "page", required = false) Integer page,
                                                  @Parameter(description = "Page size (10 by default)") @RequestParam(name = "size", required = false) Integer size) {
        return service.getAllMedications(page, size);
    }

    @Operation(summary = "Get a medication by identifier")
    @ApiResponse(responseCode = "200", description = "Found a medication")
    @GetMapping("/medications/{id}")
    public MedicationForm getOneMedication(@Parameter(description = "Identifier of medication") @PathVariable int id) {
        return service.getOneMedication(id);
    }

    @Operation(summary = "Create a new medication")
    @ApiResponse(responseCode = "200", description = "Medication was created")
    @PostMapping("/medications")
    public ResponseId<Integer> createMedication(@Parameter(description = "Form of medication") @RequestBody MedicationForm form) {
        return new ResponseId<>(service.createMedication(form));
    }
    @Operation(summary = "Update a medication by identifier")
    @ApiResponse(responseCode = "200", description = "Medication was updated")
    @PutMapping("/medications/{id}")
    public void updateMedication(@Parameter(description = "Identifier of medication") @PathVariable int id,
                                 @Parameter(description = "Medication form for updating")
                                 @RequestBody MedicationForm form) {
        service.updateMedication(id, form);
    }
    @Operation(summary = "Delete a medication by identifier")
    @ApiResponse(responseCode = "200", description = "Medication was deleted")
    @DeleteMapping("/medications/{id}")
    public void deleteMedication(@Parameter(description = "Identifier of medication") @PathVariable int id) {
        service.deleteMedication(id);
    }
}
