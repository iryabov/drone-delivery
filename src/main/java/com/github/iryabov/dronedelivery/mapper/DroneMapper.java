package com.github.iryabov.dronedelivery.mapper;

import com.github.iryabov.dronedelivery.entity.Drone;
import com.github.iryabov.dronedelivery.entity.DroneLog;
import com.github.iryabov.dronedelivery.enums.DroneState;
import com.github.iryabov.dronedelivery.model.DroneBriefInfo;
import com.github.iryabov.dronedelivery.model.DroneDetailedInfo;
import com.github.iryabov.dronedelivery.model.DroneLogInfo;
import com.github.iryabov.dronedelivery.model.DroneRegistrationForm;
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
        entity.setState(DroneState.IDLE);
        entity.setBatteryLevel(100);
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
        info.setCurrentLocation(drone.getLocation());
        info.setWeightLimit(drone.getModel().getWeightCapacity());
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
