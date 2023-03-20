package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.entity.Drone;
import com.github.iryabov.droneservice.entity.DroneModel;
import com.github.iryabov.droneservice.entity.DroneState;
import com.github.iryabov.droneservice.model.DroneBriefInfo;
import com.github.iryabov.droneservice.model.DroneRegistrationForm;
import com.github.iryabov.droneservice.repository.DroneRepository;
import com.github.iryabov.droneservice.service.DroneService;
import com.github.iryabov.droneservice.test.DroneBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DroneServiceTest {
    @Autowired
    private DroneService service;
    @Autowired
    private DroneRepository repo;

    @BeforeEach
    void setUp() {
        repo.saveAll(getTestData());
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

    private static List<Drone> getTestData() {
        List<Drone> data = new ArrayList<>();
        data.add(DroneBuilder.builder().id(1).model(DroneModel.LIGHTWEIGHT).batteryLevel(100).state(DroneState.IDLE).build());
        data.add(DroneBuilder.builder().id(2).model(DroneModel.MIDDLEWEIGHT).state(DroneState.LOADING).build());
        data.add(DroneBuilder.builder().id(3).model(DroneModel.MIDDLEWEIGHT).state(DroneState.DELIVERING).batteryLevel(15).build());
        data.add(DroneBuilder.builder().id(4).model(DroneModel.HEAVYWEIGHT).state(DroneState.IDLE).batteryLevel(20).build());
        return data;
    }

}
