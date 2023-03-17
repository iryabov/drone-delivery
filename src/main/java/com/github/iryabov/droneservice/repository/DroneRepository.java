package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.Drone;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DroneRepository extends ListPagingAndSortingRepository<Drone, Integer> {
}
