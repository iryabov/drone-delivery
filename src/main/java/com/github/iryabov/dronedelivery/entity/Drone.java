package com.github.iryabov.dronedelivery.entity;

import com.github.iryabov.dronedelivery.enums.DroneModel;
import com.github.iryabov.dronedelivery.enums.DroneState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "drone")
public class Drone {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "serial", nullable = false, unique = true, length = 100)
    private String serial;
    @Enumerated(EnumType.STRING)
    @Column(name = "model_id", nullable = false, length = 30)
    private DroneModel model;

    @Enumerated(EnumType.STRING)
    @Column(name = "state_id", nullable = false, length = 30)
    private DroneState state;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
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


