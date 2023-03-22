package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DroneModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DroneRegistrationForm {
    @Schema(description = "Drone's serial number", example = "01")
    @NotBlank(message = "Serial is required")
    @Size(max = 100, message = "Serial cannot be longer than 100 characters")
    private String serial;
    @NotNull(message = "Model is required")
    private DroneModel model;
}
