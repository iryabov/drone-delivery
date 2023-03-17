package com.github.iryabov.droneservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeliveryAddressForm {
    private String address;
    private double latitude;
    private double longitude;
}
