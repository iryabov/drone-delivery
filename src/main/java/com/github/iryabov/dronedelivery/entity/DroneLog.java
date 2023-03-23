package com.github.iryabov.dronedelivery.entity;

import com.github.iryabov.dronedelivery.enums.DroneEvent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "drone_log")
public class DroneLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "log_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime logTime;
    @ManyToOne
    @JoinColumn(name = "drone_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Drone drone;
    @Enumerated(EnumType.STRING)
    @Column(name = "event_id", nullable = false, length = 30)
    private DroneEvent event;
    @Column(name = "new_value", nullable = false)
    private String newValue;
}
