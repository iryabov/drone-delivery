package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.Drone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Integer> {
    List<Drone> findAllByBatteryLevelLessThan(int batteryLevel);
}
