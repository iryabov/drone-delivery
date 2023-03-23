package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DroneState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class DroneBriefInfo {
    private Integer id;
    @Schema(description = "Drone's name", example = "LIGHTWEIGHT-01")
    private String name;
    @Schema(description = "Current drone's state", example = "DELIVERING")
    private DroneState state;
    @Schema(description = "Current battery charge level", example = "100")
    private Integer batteryLevel;
}
