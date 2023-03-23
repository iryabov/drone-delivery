package com.github.iryabov.dronedelivery.model;

import com.github.iryabov.dronedelivery.enums.DroneEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class DroneLogInfo {
    private LocalDateTime time;
    private DroneEvent event;
    private String oldValue;
    private String newValue;
}
