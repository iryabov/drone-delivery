package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DeliveryStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ShippingInfo {
    private Integer id;
    private DeliveryStatus deliveryStatus;
    private DeliveryAddressForm destination;
    private PackageInfo packageInfo;
    private DroneBriefInfo drone;
}
