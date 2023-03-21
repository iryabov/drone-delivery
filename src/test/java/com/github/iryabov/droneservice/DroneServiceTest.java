package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.entity.*;
import com.github.iryabov.droneservice.exception.DroneDeliveryException;
import com.github.iryabov.droneservice.model.DroneBriefInfo;
import com.github.iryabov.droneservice.model.DroneLogInfo;
import com.github.iryabov.droneservice.model.DroneRegistrationForm;
import com.github.iryabov.droneservice.repository.DroneLogRepository;
import com.github.iryabov.droneservice.repository.DroneRepository;
import com.github.iryabov.droneservice.service.DroneService;
import com.github.iryabov.droneservice.test.DroneBuilder;
import com.github.iryabov.droneservice.test.DroneLogBuilder;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DroneServiceTest {
    @Autowired
    private DroneService service;
    @Autowired
    private DroneRepository droneRepo;
    @Autowired
    private DroneLogRepository droneLogRepo;

    @BeforeEach
    void setUp() {
        droneRepo.saveAll(getTestData());
    }

    @Test
    void simpleCRUD() {
        int droneId = service.create(DroneRegistrationForm.builder()
                .serial("01")
                .model(DroneModel.LIGHTWEIGHT)
                .build());

        var drones = service.getAllByStateAndModel(null, DroneModel.LIGHTWEIGHT);
        assertThat(drones.stream().map(DroneBriefInfo::getId).collect(toList()), hasItem(droneId));

        var drone = service.getDetailedInfo(droneId);
        assertThat(drone.getId(), is(droneId));
        assertThat(drone.getName(), is("LIGHTWEIGHT-01"));

        service.delete(droneId);
        drones = service.getAllByStateAndModel(null, DroneModel.LIGHTWEIGHT);
        assertThat(drones.stream().map(DroneBriefInfo::getId).collect(toList()), not(hasItem(droneId)));
    }

    @Test
    void getAllByStateAndModel() {
        var drones = service.getAllByStateAndModel(DroneState.LOADING, DroneModel.MIDDLEWEIGHT);
        assertThat(drones.size(), is(1));

        drones = service.getAllByStateAndModel(null, DroneModel.MIDDLEWEIGHT);
        assertThat(drones.size(), is(2));

        drones = service.getAllByStateAndModel(DroneState.IDLE, null);
        assertThat(drones.size(), is(2));
        assertThat(drones.stream().map(DroneBriefInfo::getState).collect(toList()), everyItem(is(DroneState.IDLE)));
    }

    @Test
    void getAllWithLowBattery() {
        var drones = service.getAllWithLowBattery();
        assertThat(drones.size(), is(2));
    }


    @Test
    void getEventLogs() {
        droneLogRepo.saveAll(getTestEventLogsData());

        var logs = service.getEventLogs(1,
                LocalDateTime.of(2023, 3, 20, 15, 0),
                LocalDateTime.of(2023, 3, 20, 16, 0),
                DroneEvent.BATTERY_CHANGE);
        assertThat(logs.size(), is(2));
        assertThat(logs.stream().map(DroneLogInfo::getEvent).collect(toList()), everyItem(is(DroneEvent.BATTERY_CHANGE)));
        assertThat(logs.get(0).getOldValue(), nullValue());
        assertThat(logs.get(0).getNewValue(), is("100"));
        assertThat(logs.get(1).getOldValue(), is("100"));
        assertThat(logs.get(1).getNewValue(), is("90"));


        logs = service.getEventLogs(1,
                LocalDateTime.of(2023, 3, 20,16 ,0),
                LocalDateTime.of(2023, 3, 20, 17, 0),
                DroneEvent.LOCATION_CHANGE);
        assertThat(logs.size(), is(2));
        assertThat(logs.stream().map(DroneLogInfo::getEvent).collect(toList()), everyItem(is(DroneEvent.LOCATION_CHANGE)));
        assertThat(logs.get(0).getOldValue(), nullValue());
        assertThat(logs.get(0).getNewValue(), is("(0, 0)"));
        assertThat(logs.get(1).getOldValue(), is("(0, 0)"));
        assertThat(logs.get(1).getNewValue(), is("(10, 10)"));

        logs = service.getEventLogs(1,
                LocalDateTime.of(2023, 3, 20,17 ,0),
                LocalDateTime.of(2023, 3, 20, 18, 0),
                DroneEvent.STATE_CHANGE);
        assertThat(logs.size(), is(2));
        assertThat(logs.stream().map(DroneLogInfo::getEvent).collect(toList()), everyItem(is(DroneEvent.STATE_CHANGE)));
        assertThat(logs.get(0).getOldValue(), nullValue());
        assertThat(logs.get(0).getNewValue(), is(DroneState.LOADING.name()));
        assertThat(logs.get(1).getOldValue(), is(DroneState.LOADING.name()));
        assertThat(logs.get(1).getNewValue(), is(DroneState.LOADED.name()));
    }

    @Test
    public void validationsOnCreating() {
        //Not valid, because the serial isn't set
        assertThrows(ConstraintViolationException.class, () -> {
            service.create(DroneRegistrationForm.builder()
                    .model(DroneModel.LIGHTWEIGHT)
                    .build());
        });

        //Not valid, because the model isn't set
        assertThrows(ConstraintViolationException.class, () -> {
            service.create(DroneRegistrationForm.builder()
                    .serial("AA-01")
                    .build());
        });

        //Not valid, because the serial is too long
        assertThrows(ConstraintViolationException.class, () -> {
            service.create(DroneRegistrationForm.builder()
                    .serial("QWERTYUIOPASDFGHJKLZXCVBNM1234567890QWERTYUIOPASDFGHJKLZXCVBNM1234567890QWERTYUIOPASDFGHJKLZXCVBNM1234567890")
                    .model(DroneModel.LIGHTWEIGHT)
                    .build());
        });

        //Not valid, because the serial is not unique
        assertThrows(DroneDeliveryException.class, () -> {
            service.create(DroneRegistrationForm.builder()
                    .serial("01")
                    .model(DroneModel.LIGHTWEIGHT)
                    .build());
        });
    }

    private static List<Drone> getTestData() {
        List<Drone> data = new ArrayList<>();
        data.add(DroneBuilder.builder().id(1).serial("01").model(DroneModel.LIGHTWEIGHT).batteryLevel(100).state(DroneState.IDLE).build());
        data.add(DroneBuilder.builder().id(2).serial("02").model(DroneModel.MIDDLEWEIGHT).state(DroneState.LOADING).build());
        data.add(DroneBuilder.builder().id(3).serial("03").model(DroneModel.MIDDLEWEIGHT).state(DroneState.DELIVERING).batteryLevel(15).build());
        data.add(DroneBuilder.builder().id(4).serial("04").model(DroneModel.HEAVYWEIGHT).state(DroneState.IDLE).batteryLevel(20).build());
        return data;
    }

    private static List<DroneLog> getTestEventLogsData() {
        List<DroneLog> data = new ArrayList<>();
        data.add(DroneLogBuilder.builder()
                .droneId(1)
                .event(DroneEvent.BATTERY_CHANGE)
                .time(LocalDateTime.of(2023, 3, 20, 15, 10))
                .newValue("100")
                .build());
        data.add(DroneLogBuilder.builder()
                .droneId(1)
                .event(DroneEvent.BATTERY_CHANGE)
                .time(LocalDateTime.of(2023, 3, 20, 15, 15))
                .newValue("90")
                .build());

        data.add(DroneLogBuilder.builder()
                .droneId(1)
                .event(DroneEvent.LOCATION_CHANGE)
                .time(LocalDateTime.of(2023, 3, 20, 16, 10))
                .newValue("(0, 0)")
                .build());
        data.add(DroneLogBuilder.builder()
                .droneId(1)
                .event(DroneEvent.LOCATION_CHANGE)
                .time(LocalDateTime.of(2023, 3, 20, 16, 15))
                .newValue("(10, 10)")
                .build());

        data.add(DroneLogBuilder.builder()
                .droneId(1)
                .event(DroneEvent.STATE_CHANGE)
                .time(LocalDateTime.of(2023, 3, 20, 17, 10))
                .newValue(DroneState.LOADING.name())
                .build());
        data.add(DroneLogBuilder.builder()
                .droneId(1)
                .event(DroneEvent.STATE_CHANGE)
                .time(LocalDateTime.of(2023, 3, 20, 17, 15))
                .newValue(DroneState.LOADED.name())
                .build());
        return data;
    }

}
