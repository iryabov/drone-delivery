package com.github.iryabov.droneservice.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeliveryAddressForm {
    private String address;
    @Min(value = -90, message = "Latitude cannot be less than -90")
    @Max(value = 90, message = "Latitude cannot be greater than +90")
    private double latitude;
    @Min(value = -90, message = "Longitude cannot be less than -90")
    @Max(value = 90, message = "Longitude cannot be greater than +90")
    private double longitude;
}
