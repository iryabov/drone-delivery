package com.github.iryabov.dronedelivery.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DeliveryAddressForm {
    @Schema(description = "Delivery address", example = "бул. Драган Цанков 36, София, Болгария")
    private String address;
    @Schema(description = "Latitude of destination", example = "42.67034")
    @Min(value = -180, message = "Latitude cannot be less than -180")
    @Max(value = 180, message = "Latitude cannot be greater than +180")
    private double latitude;
    @Schema(description = "Longitude of destination", example = "23.35111")
    @Min(value = -180, message = "Longitude cannot be less than -180")
    @Max(value = 180, message = "Longitude cannot be greater than +180")
    private double longitude;
}
