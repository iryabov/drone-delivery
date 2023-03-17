package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DroneModel;
import com.github.iryabov.droneservice.entity.Location;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DroneDetailedInfo extends DroneBriefInfo {
    private String serial;
    private DroneModel droneModel;
    private Integer weightLimit;
    private Location currentLocation;
}
