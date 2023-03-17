package com.github.iryabov.droneservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "drone")
public class Drone {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "serial")
    private String serial;
    @ManyToOne
    @JoinColumn(name = "model_id")
    private DroneModel model;
    @Column(name = "weight_limit")
    private Integer weightLimit;
    @ManyToOne
    @JoinColumn(name = "state_id")
    private DroneState state;
    @ManyToOne
    @JoinColumn(name = "shipping_id")
    private Shipping shipping;
    @Column(name = "battery_level")
    private Integer batteryLevel;
}


