package com.github.iryabov.droneservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MedicationForm {
    private Integer id;
    @Schema(description = "Name of medication", example = "Penicillin")
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "[a-zA-Z0-9\\-_]+", message = "Name must contain only letters, numbers, ‘-‘, ‘_’")
    private String name;
    @Schema(description = "Code of medication", example = "PEN")
    @NotBlank(message = "Code is required")
    @Pattern(regexp = "[A-Z0-9_]+", message = "Code must contain only uppercase letters, numbers, ‘_’")
    private String code;
    @Schema(description = "Weight of medication in grams", example = "0.05")
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than 0")
    private Double weight;
    @Schema(description = "Identifier of image file in the image store", example = "1")
    private Long imageId;
}
