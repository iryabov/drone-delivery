package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DroneEvent;

import java.time.LocalDateTime;

public class DroneLogInfo {
    private LocalDateTime time;
    private DroneEvent event;
    private String oldValue;
    private String newValue;
}
