package com.github.iryabov.droneservice.client.impl;

import com.github.iryabov.droneservice.client.DroneClient;
import com.github.iryabov.droneservice.entity.DroneModel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class StubDroneClient implements DroneClient {
    private static final Point BASE = new Point(0, 0);
    private final Map<String, DroneEmulator> fleet = new ConcurrentHashMap<>();

    public StubDroneClient() {
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> {
            for (Map.Entry<String, DroneEmulator> entry : fleet.entrySet()) {
                DroneEmulator drone = entry.getValue();
                drone.compute(1);
                System.out.println("Drone " + entry.getKey()
                        + " location: (" + drone.getLocation().getLat() + "," + drone.getLocation().getLon() + ")"
                        + ", battery: " + drone.getBatteryLevel()
                        + ", loading: " + drone.getLoadingPercentage()
                );
            }
        }, 1L, 1L, TimeUnit.SECONDS);
    }

    @Override
    public Driver lookup(String droneSerial) {
        return fleet.get(droneSerial);
    }

    public void add(String serial, DroneModel model) {
        DroneEmulator drone = new DroneEmulator(model.getFlySpeed(), model.getWeightCapacity(), model.getBatteryCapacity(), BASE);
        fleet.put(serial, drone);
    }
}
