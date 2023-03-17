package com.github.iryabov.droneservice.entity;

import jakarta.persistence.Entity;

@Entity
public enum DroneEvent {
    STATE_CHANGE,
    BATTERY_CHANGE,
    LOCATION_CHANGE,
}
