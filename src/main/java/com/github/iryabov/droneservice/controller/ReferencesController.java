package com.github.iryabov.droneservice.controller;

import com.github.iryabov.droneservice.model.MedicationForm;
import com.github.iryabov.droneservice.service.ReferencesService;
import com.github.iryabov.droneservice.model.ResponseId;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/references")
@AllArgsConstructor
public class ReferencesController {
    private ReferencesService service;
    @GetMapping("/medications")
    public List<MedicationForm> getAllMedications() {
        return service.getAllMedications();
    }
    @GetMapping("/medications/{id}")
    public MedicationForm getOneMedication(@PathVariable int id) {
        return service.getOneMedication(id);
    }
    @PostMapping("/medications")
    public ResponseId<Integer> createMedication(@RequestBody MedicationForm form) {
        return new ResponseId<>(service.createMedication(form));
    }
    @PutMapping("/medications/{id}")
    public void updateMedication(@PathVariable int id, @RequestBody MedicationForm form) {
        service.updateMedication(id, form);
    }
    @DeleteMapping("/medications/{id}")
    public void deleteMedication(@PathVariable int id) {
        service.deleteMedication(id);
    }
}
