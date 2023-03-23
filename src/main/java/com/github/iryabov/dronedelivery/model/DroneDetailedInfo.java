package com.github.iryabov.dronedelivery.model;

import com.github.iryabov.dronedelivery.enums.DroneModel;
import com.github.iryabov.dronedelivery.entity.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DroneDetailedInfo extends DroneBriefInfo {
    @Schema(description = "Drone's serial number", example = "01")
    private String serial;
    @Schema(description = "Drone's model", example = "LIGHTWEIGHT")
    private DroneModel droneModel;
    @Schema(description = "Max weight (kg)", example = "0.5")
    private Double weightLimit;
    @Schema(description = "Current drone's coordinates")
    private Location currentLocation;
}
