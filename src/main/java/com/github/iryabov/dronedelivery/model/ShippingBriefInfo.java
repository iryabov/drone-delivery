package com.github.iryabov.dronedelivery.model;

import com.github.iryabov.dronedelivery.entity.Location;
import com.github.iryabov.dronedelivery.enums.DeliveryStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ShippingBriefInfo {
    private Integer id;
    private DeliveryStatus deliveryStatus;
    private String deliveryAddress;
    private Location destination;
}
