package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DroneState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DroneBriefInfo {
    private Integer id;
    private String name;
    private DroneState state;
    private Integer batteryLevel;
}