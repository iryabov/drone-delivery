package com.github.iryabov.droneservice.job;

import com.github.iryabov.droneservice.client.DroneClient;
import com.github.iryabov.droneservice.entity.Drone;
import com.github.iryabov.droneservice.entity.DroneEvent;
import com.github.iryabov.droneservice.entity.DroneLog;
import com.github.iryabov.droneservice.entity.DroneState;
import com.github.iryabov.droneservice.repository.DroneLogRepository;
import com.github.iryabov.droneservice.repository.DroneRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@Transactional
@AllArgsConstructor
public class DroneLogJob {
    private final DroneRepository droneRepo;
    private final DroneLogRepository droneLogRepo;
    private final DroneClient droneClient;

    @Scheduled(fixedDelayString = "${drone.logs.battery_level.fixed_delay}",
            initialDelayString = "${drone.logs.battery_level.initial_delay}")
    public void batteryLevelLog() {
        log(droneRepo.findAll(), DroneEvent.BATTERY_CHANGE, DroneClient.Driver::getBatteryLevel);
    }

    @Scheduled(fixedDelayString = "${drone.logs.location.fixed_delay}",
            initialDelayString = "${drone.logs.location.initial_delay}")
    public void locationLog() {
        log(droneRepo.findAll(), DroneEvent.LOCATION_CHANGE, DroneClient.Driver::getLocation);
    }

    @Scheduled(fixedDelayString = "${drone.logs.state_changed.fixed_delay}",
            initialDelayString = "${drone.logs.state_changed.initial_delay}")
    public void stateChangedLog() {
        loadingLog();
        returningLog();
    }

    private void loadingLog() {
        Drone criteria = new Drone();
        criteria.setState(DroneState.LOADING);
        log(droneRepo.findAll(Example.of(criteria)), DroneEvent.STATE_CHANGE, driver -> {
            if (driver.getLoadingPercentage() == 100) {
                return DroneState.LOADED;
            } else {
                return null;
            }
        });
    }

    private void returningLog() {
        Drone criteria = new Drone();
        criteria.setState(DroneState.RETURNING);
        log(droneRepo.findAll(Example.of(criteria)), DroneEvent.STATE_CHANGE, driver -> {
            if (driver.isOnBase()) {
                return DroneState.IDLE;
            } else {
                return null;
            }
        });
    }


    private void log(List<Drone> drones, DroneEvent event, Function<DroneClient.Driver, Object> valueGetter) {
        List<DroneLog> logs = new ArrayList<>();
        for (Drone drone : drones) {
            DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
            Object newValue = valueGetter.apply(driver);
            if (newValue == null)
                continue;

            DroneLog log = new DroneLog();
            log.setDrone(drone);
            log.setLogTime(LocalDateTime.now());
            log.setEvent(event);
            log.setNewValue(String.valueOf(newValue));
            logs.add(log);
        }
        if (!logs.isEmpty())
            droneLogRepo.saveAll(logs);
    }
}
