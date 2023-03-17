package com.github.iryabov.droneservice.model;

import com.github.iryabov.droneservice.entity.DroneModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DroneRegistrationForm {
    private String serial;
    private DroneModel model;
    private Integer weightLimit;
}
