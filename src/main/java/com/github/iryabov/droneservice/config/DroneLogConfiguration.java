package com.github.iryabov.droneservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "drone.logs.enabled", havingValue = "true")
public class DroneLogConfiguration {
}
