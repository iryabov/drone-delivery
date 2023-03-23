package com.github.iryabov.droneservice.service;

import com.github.iryabov.droneservice.entity.DroneEvent;
import com.github.iryabov.droneservice.entity.DroneModel;
import com.github.iryabov.droneservice.entity.DroneState;
import com.github.iryabov.droneservice.model.DroneBriefInfo;
import com.github.iryabov.droneservice.model.DroneDetailedInfo;
import com.github.iryabov.droneservice.model.DroneLogInfo;
import com.github.iryabov.droneservice.model.DroneRegistrationForm;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Drone control service
 */
public interface DroneService {
    /**
     * Register a new drone
     * @param registrationForm Registration form
     * @return Identifier of a new drone
     */
    int create(DroneRegistrationForm registrationForm);

    /**
     * Delete drone
     * @param droneId Drone identifier
     */
    void delete(int droneId);

    /**
     * Get detailed information about a drone
     * @param droneId Drone identifier
     * @return Detailed information
     */
    DroneDetailedInfo getDetailedInfo(int droneId);

    /**
     * Get drone event logs
     * @param droneId Drone identifier
     * @param from Date and time from (from an hour ago by default)
     * @param till Date and time until (until now by default)
     * @param event Which event
     * @return List of logs
     */
    List<DroneLogInfo> getEventLogs(int droneId, @Nullable LocalDateTime from, @Nullable LocalDateTime till, DroneEvent event);

    /**
     * Get all drones
     * @param state State of drone
     * @param model Model of drone
     * @return List of drones
     */
    List<DroneBriefInfo> getAllByStateAndModel(@Nullable DroneState state, @Nullable DroneModel model);

    /**
     * Get all drones with low battery charge
     * @return List of drones
     */
    List<DroneBriefInfo> getAllWithLowBattery();
}
