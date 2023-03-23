package com.github.iryabov.dronedelivery.model;

import com.github.iryabov.dronedelivery.enums.DeliveryStatus;
import com.github.iryabov.dronedelivery.entity.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ShippingInfo {
    private Integer id;
    private DeliveryStatus deliveryStatus;
    private String deliveryAddress;
    private Location destination;
    private PackageInfo packageInfo;
    private DroneBriefInfo drone;
}
