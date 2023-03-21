package com.github.iryabov.droneservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "shipping")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "destination_lat", column = @Column(name = "latitude")),
            @AttributeOverride(name = "destination_lon", column = @Column(name = "longitude"))
    })
    private Location destination;
    @Column(name = "delivery_address")
    private String deliveryAddress;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_id")
    private DeliveryStatus status;
    @OneToMany(mappedBy = "shipping", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PackageItem> items;
    @OneToOne(mappedBy = "shipping")
    private Drone drone;
}
