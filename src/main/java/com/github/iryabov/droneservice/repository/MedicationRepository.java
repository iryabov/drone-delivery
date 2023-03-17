package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.Medication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationRepository extends CrudRepository<Medication, Integer> {

}
