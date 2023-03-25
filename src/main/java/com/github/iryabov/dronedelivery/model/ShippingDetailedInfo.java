package com.github.iryabov.dronedelivery.model;

import com.github.iryabov.dronedelivery.enums.DeliveryStatus;
import com.github.iryabov.dronedelivery.entity.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ShippingDetailedInfo extends ShippingBriefInfo {
    @Schema(description = "Package information")
    private PackageInfo packageInfo;
}
