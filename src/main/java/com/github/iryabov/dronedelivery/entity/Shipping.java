package com.github.iryabov.dronedelivery.entity;

import com.github.iryabov.dronedelivery.enums.DeliveryStatus;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "destination_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "destination_lon"))
    })
    private Location destination;
    @Column(name = "delivery_address")
    private String deliveryAddress;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_id", length = 30)
    private DeliveryStatus status;
    @OneToMany(mappedBy = "shipping", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<PackageItem> items;
    @OneToOne(mappedBy = "shipping")
    private Drone drone;
}
