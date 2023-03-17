package com.github.iryabov.droneservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "drone_log")
public class DroneLog {
    @Id
    private Long id;
    @Column(name = "log_time")
    private LocalDateTime logTime;
    @ManyToOne
    @JoinColumn(name = "drone_id")
    private Drone drone;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private DroneEvent event;
    @Column(name = "new_value")
    private String newValue;
}
