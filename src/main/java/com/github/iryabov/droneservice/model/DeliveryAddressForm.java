package com.github.iryabov.droneservice.model;

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
    @Min(value = -90, message = "Latitude cannot be less than -90")
    @Max(value = 90, message = "Latitude cannot be greater than +90")
    private double latitude;
    @Schema(description = "Longitude of destination", example = "23.35111")
    @Min(value = -90, message = "Longitude cannot be less than -90")
    @Max(value = 90, message = "Longitude cannot be greater than +90")
    private double longitude;
}
