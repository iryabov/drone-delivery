package com.github.iryabov.droneservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MedicationForm {
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "[a-zA-Z0-9\\-_]+", message = "Name must contain only letters, numbers, ‘-‘, ‘_’")
    private String name;
    @NotBlank(message = "Code is required")
    @Pattern(regexp = "[A-Z0-9_]+", message = "Code must contain only uppercase letters, numbers, ‘_’")
    private String code;
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than 0")
    private Double weight;
}
