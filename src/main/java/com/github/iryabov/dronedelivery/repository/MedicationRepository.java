package com.github.iryabov.dronedelivery.repository;

import com.github.iryabov.dronedelivery.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Integer> {

}
