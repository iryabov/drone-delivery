package com.github.iryabov.droneservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "drone_model")
public enum DroneModel {
    LIGHTWEIGHT,
    MIDDLEWEIGHT,
    CRUISEWEIGHT,
    HEAVYWEIGHT
}
