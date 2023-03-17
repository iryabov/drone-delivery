package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShippingInfo {
    private DeliveryStatus deliveryStatus;
    private DeliveryAddressForm destination;
    private PackageForm packageInfo;
    private DroneBriefInfo drone;
}
