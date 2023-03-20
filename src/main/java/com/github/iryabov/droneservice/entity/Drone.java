package com.github.iryabov.droneservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "drone")
public class Drone {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "serial")
    private String serial;
    @Enumerated(EnumType.STRING)
    @Column(name = "model_id")
    private DroneModel model;
    @Column(name = "weight_limit")
    private Double weightLimit;
    @Enumerated(EnumType.STRING)
    @Column(name = "state_id")
    private DroneState state;
    @OneToOne
    @JoinColumn(name = "shipping_id")
    private Shipping shipping;
    @Column(name = "battery_level")
    private Integer batteryLevel;
}


