package com.github.iryabov.dronedelivery.enums;

import lombok.Getter;

@Getter
public enum DroneModel {
    LIGHTWEIGHT(16, 0.5, 2000),
    MIDDLEWEIGHT(12, 1, 3000),
    CRUISEWEIGHT(10, 1.5, 4000),
    HEAVYWEIGHT(6, 3, 5000);

    private final double flySpeed;
    private final double weightCapacity;
    private final int batteryCapacity;

    DroneModel(double flySpeed, double weightCapacity, int batteryCapacity) {
        this.flySpeed = flySpeed;
        this.weightCapacity = weightCapacity;
        this.batteryCapacity = batteryCapacity;
    }
}
