package com.github.iryabov.droneservice.test;

import com.github.iryabov.droneservice.entity.Drone;
import com.github.iryabov.droneservice.entity.DroneModel;
import com.github.iryabov.droneservice.entity.DroneState;

public class DroneBuilder {
    private final Drone drone;

    public DroneBuilder(Drone drone) {
        this.drone = drone;
    }

    public static DroneBuilder builder() {
        return new DroneBuilder(new Drone());
    }

    public Drone build() {
        return drone;
    }

    public DroneBuilder id(int id) {
        drone.setId(id);
        drone.setSerial(String.format("%dd", id));
        return this;
    }

    public DroneBuilder model(DroneModel model) {
        drone.setModel(model);
        return this;
    }

    public DroneBuilder batteryLevel(int batteryLevel) {
        drone.setBatteryLevel(batteryLevel);
        return this;
    }

    public DroneBuilder state(DroneState state) {
        drone.setState(state);
        return this;
    }
}
