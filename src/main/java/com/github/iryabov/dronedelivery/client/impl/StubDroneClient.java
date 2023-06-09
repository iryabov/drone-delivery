package com.github.iryabov.dronedelivery.client.impl;

import com.github.iryabov.dronedelivery.client.DroneClient;
import com.github.iryabov.dronedelivery.enums.DroneModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class StubDroneClient implements DroneClient, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger("DroneEmulator");
    private final Map<String, DroneEmulator> fleet = new ConcurrentHashMap<>();
    @Value("${drone.base.latitude}")
    private Double baseLatitude;
    @Value("${drone.base.longitude}")
    private Double baseLongitude;

    public StubDroneClient() {
    }

    public StubDroneClient(Double baseLatitude, Double baseLongitude) {
        this.baseLatitude = baseLatitude;
        this.baseLongitude = baseLongitude;
    }

    @Override
    public Driver lookup(String droneSerial, DroneModel model) {
        return fleet.compute(droneSerial, (s, d) -> Objects.requireNonNullElseGet(d,
                () -> new DroneEmulator(model.getFlySpeed(), model.getWeightCapacity(), model.getBatteryCapacity(),
                        new Point(baseLatitude, baseLongitude))));
    }

    @Override
    public void afterPropertiesSet() {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            for (Map.Entry<String, DroneEmulator> entry : fleet.entrySet()) {
                DroneEmulator drone = entry.getValue();
                drone.compute(1);
                logger.debug("Drone " + entry.getKey()
                        + " location " + drone.getLocation()
                        + ", battery " + drone.getBatteryLevel() + "%"
                        + ", loading " + drone.getLoadingPercentage() + "%"
                );
            }
        }, 1L, 1L, TimeUnit.SECONDS);
    }
}
