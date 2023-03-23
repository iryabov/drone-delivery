package com.github.iryabov.droneservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "shipping_log")
public class ShippingLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "log_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime logTime;
    @ManyToOne
    @JoinColumn(name = "drone_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Drone drone;
    @ManyToOne
    @JoinColumn(name = "shipping_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Shipping shipping;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_id", length = 30, nullable = false)
    private ShippingEvent event;
    @Column(name = "new_value", nullable = false)
    private String newValue;
}
