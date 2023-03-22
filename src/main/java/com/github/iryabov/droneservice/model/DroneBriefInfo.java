package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DroneState;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class DroneBriefInfo {
    private Integer id;
    private String name;
    private DroneState state;
    private Integer batteryLevel;
    private Integer weightLimit;
}
