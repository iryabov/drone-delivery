package com.github.iryabov.droneservice.test;

import com.github.iryabov.droneservice.entity.*;
import lombok.val;

import java.time.LocalDateTime;

public class DroneLogBuilder {

    private final DroneLog droneLog;

    public DroneLogBuilder(DroneLog droneLog) {
        this.droneLog = droneLog;
    }

    public static DroneLogBuilder builder() {
        return new DroneLogBuilder(new DroneLog());
    }

    public DroneLog build() {
        return droneLog;
    }

    public DroneLogBuilder time(LocalDateTime time) {
        droneLog.setLogTime(time);
        return this;
    }

    public DroneLogBuilder event(DroneEvent event) {
        droneLog.setEvent(event);
        return this;
    }

    public DroneLogBuilder newValue(String newValue) {
        droneLog.setNewValue(newValue);
        return this;
    }

    public DroneLogBuilder droneId(int droneId) {
        Drone drone = new Drone();
        drone.setId(droneId);
        droneLog.setDrone(drone);
        return this;
    }
}
