package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.ShippingEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
public class ShippingLogInfo {
    private LocalDateTime time;
    private ShippingEvent event;
    private String oldValue;
    private String newValue;
}
