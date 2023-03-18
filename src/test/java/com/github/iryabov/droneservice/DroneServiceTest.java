package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.entity.DroneModel;
import com.github.iryabov.droneservice.entity.DroneState;
import com.github.iryabov.droneservice.model.DroneBriefInfo;
import com.github.iryabov.droneservice.model.DroneRegistrationForm;
import com.github.iryabov.droneservice.service.DroneService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DroneServiceTest {
    @Autowired
    private DroneService service;

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
}
