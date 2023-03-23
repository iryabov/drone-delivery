package com.github.iryabov.dronedelivery.repository;

import com.github.iryabov.dronedelivery.entity.Drone;
import com.github.iryabov.dronedelivery.enums.DroneState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Integer> {
    List<Drone> findAllByBatteryLevelLessThan(int batteryLevel);

    List<Drone> findAllByStateAndBatteryLevelGreaterThan(DroneState state, Integer batteryLevel);
}
