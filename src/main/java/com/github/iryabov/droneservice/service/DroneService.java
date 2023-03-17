package com.github.iryabov.droneservice.service;

import com.github.iryabov.droneservice.entity.DroneModel;
import com.github.iryabov.droneservice.entity.DroneState;
import com.github.iryabov.droneservice.model.DroneBriefInfo;
import com.github.iryabov.droneservice.model.DroneDetailedInfo;
import com.github.iryabov.droneservice.model.DroneLogInfo;
import com.github.iryabov.droneservice.model.DroneRegistrationForm;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface DroneService {
    int create(DroneRegistrationForm registrationForm);
    void delete(int droneId);
    DroneDetailedInfo getDetailedInfo(int droneId);
    List<DroneLogInfo> getEventLogs(int droneId, LocalDateTime from, LocalDateTime till);
    List<DroneBriefInfo> getAllByStateAndModel(DroneState state, DroneModel model);
    List<DroneBriefInfo> getAllByLowBattery();
}
