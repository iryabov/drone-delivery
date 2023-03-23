package com.github.iryabov.dronedelivery.model;

import com.github.iryabov.dronedelivery.enums.ShippingEvent;
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
