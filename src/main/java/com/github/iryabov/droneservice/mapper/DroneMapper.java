package com.github.iryabov.droneservice.mapper;

import com.github.iryabov.droneservice.entity.Drone;
import com.github.iryabov.droneservice.entity.DroneLog;
import com.github.iryabov.droneservice.model.DroneBriefInfo;
import com.github.iryabov.droneservice.model.DroneDetailedInfo;
import com.github.iryabov.droneservice.model.DroneLogInfo;
import com.github.iryabov.droneservice.model.DroneRegistrationForm;
import org.springframework.stereotype.Component;

@Component
public class DroneMapper {
    public DroneBriefInfo toBriefInfo(Drone entity) {
        DroneBriefInfo info = new DroneBriefInfo();
        info.setId(entity.getId());
        info.setName(entity.getModel() + "-" + entity.getSerial());
        info.setState(entity.getState());
        info.setBatteryLevel(entity.getBatteryLevel());
        return info;
    }


    public Drone toEntity(DroneRegistrationForm form) {
        Drone entity = new Drone();
        entity.setSerial(form.getSerial());
        entity.setModel(form.getModel());
        return entity;
    }

    public DroneDetailedInfo toDetailedInfo(Drone drone) {
        DroneDetailedInfo info = new DroneDetailedInfo();
        info.setId(drone.getId());
        info.setSerial(drone.getSerial());
        info.setDroneModel(drone.getModel());
        info.setName(drone.getModel() + "-" + drone.getSerial());
        info.setState(drone.getState());
        info.setBatteryLevel(drone.getBatteryLevel());
        return info;
    }

    public DroneLogInfo toDroneLogInfo(DroneLog entity) {
        DroneLogInfo info = new DroneLogInfo();
        info.setTime(entity.getLogTime());
        info.setEvent(entity.getEvent());
        info.setNewValue(entity.getNewValue());
        return info;
    }
}
