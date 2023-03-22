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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "serial", nullable = false)
    private String serial;
    @Enumerated(EnumType.STRING)
    @Column(name = "model_id", nullable = false)
    private DroneModel model;
    @Enumerated(EnumType.STRING)
    @Column(name = "state_id", nullable = false)
    private DroneState state;
    @OneToOne
    @JoinColumn(name = "shipping_id")
    private Shipping shipping;
    @Column(name = "battery_level", nullable = false)
    private Integer batteryLevel;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "location_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "location_lon"))
    })
    private Location location;
}


