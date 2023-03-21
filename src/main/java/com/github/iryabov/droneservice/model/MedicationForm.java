package com.github.iryabov.droneservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MedicationForm {
    private String name;
    private String code;
    private Double weight;
}
