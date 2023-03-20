package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DroneEvent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DroneLogInfo {
    private LocalDateTime time;
    private DroneEvent event;
    private String oldValue;
    private String newValue;
}
