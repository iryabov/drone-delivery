package com.github.iryabov.dronedelivery.model;

import com.github.iryabov.dronedelivery.enums.DeliveryStatus;
import com.github.iryabov.dronedelivery.entity.Location;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ShippingDetailedInfo extends ShippingBriefInfo {
    private PackageInfo packageInfo;
    private DroneBriefInfo drone;
}
