package com.github.iryabov.droneservice.entity;

import jakarta.persistence.Entity;

public enum DroneState {
    IDLE,
    LOADING,
    LOADED,
    DELIVERING,
    DELIVERED,
    RETURNING
}
