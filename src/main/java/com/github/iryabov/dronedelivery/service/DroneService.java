package com.github.iryabov.dronedelivery.service;

import com.github.iryabov.dronedelivery.enums.DroneEvent;
import com.github.iryabov.dronedelivery.enums.DroneModel;
import com.github.iryabov.dronedelivery.enums.DroneState;
import com.github.iryabov.dronedelivery.model.DroneBriefInfo;
import com.github.iryabov.dronedelivery.model.DroneDetailedInfo;
import com.github.iryabov.dronedelivery.model.DroneLogInfo;
import com.github.iryabov.dronedelivery.model.DroneRegistrationForm;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Pageable;

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
     * @param event Which event
     * @param from Date and time from (from an hour ago by default)
     * @param till Date and time until (until now by default)
     * @param page Page number
     * @param size Page size
     * @return List of logs
     */
    List<DroneLogInfo> getEventLogs(int droneId,
                                    DroneEvent event,
                                    @Nullable LocalDateTime from,
                                    @Nullable LocalDateTime till,
                                    @Nullable Integer page,
                                    @Nullable Integer size);

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
