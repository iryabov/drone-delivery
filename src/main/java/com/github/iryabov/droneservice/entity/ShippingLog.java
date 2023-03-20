package com.github.iryabov.droneservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "shipping_log")
public class ShippingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "log_time")
    private LocalDateTime logTime;
    @ManyToOne
    @JoinColumn(name = "drone_id")
    private Drone drone;
    @ManyToOne
    @JoinColumn(name = "shipping_id")
    private Shipping shipping;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_id")
    private ShippingEvent event;
    @Column(name = "new_value")
    private String newValue;
}
