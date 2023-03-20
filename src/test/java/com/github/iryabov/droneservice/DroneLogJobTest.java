package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.client.DroneClient;
import com.github.iryabov.droneservice.entity.*;
import com.github.iryabov.droneservice.job.DroneLogJob;
import com.github.iryabov.droneservice.repository.DroneLogRepository;
import com.github.iryabov.droneservice.repository.DroneRepository;
import com.github.iryabov.droneservice.test.DroneBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "drone.logs.enabled=false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DroneLogJobTest {
    @Autowired
    private DroneLogJob job;
    @Autowired
    private DroneLogRepository droneLogRepo;
    @Autowired
    private DroneRepository droneRepo;
    @MockBean
    private DroneClient droneClient;
    @Mock
    private DroneClient.Driver driver;

    @BeforeEach
    void setUp() {
        given(this.droneClient.lookup(anyString(), any())).willReturn(driver);
        droneRepo.saveAll(getTestData());
    }

    @Test
    void batteryLevelJob() {
        when(driver.getBatteryLevel()).thenReturn(100, 90, 80, 70);
        job.batteryLevelLog();

        var criteria = new DroneLog();
        criteria.setEvent(DroneEvent.BATTERY_CHANGE);
        List<DroneLog> logs = droneLogRepo.findAll(Example.of(criteria));
        assertThat(logs.size(), is(4));
        assertThat(logs.stream().map(DroneLog::getLogTime).collect(toList()), everyItem(lessThan(LocalDateTime.now())));
        assertThat(logs.stream().map(DroneLog::getNewValue).collect(toList()), everyItem(notNullValue()));
    }

    @Test
    void locationJob() {
        when(driver.getLocation()).thenReturn(
                new DroneClient.Point(0, 0),
                new DroneClient.Point(10, 10),
                new DroneClient.Point(20, 20),
                new DroneClient.Point(30, 30));
        job.locationLog();

        var criteria = new DroneLog();
        criteria.setEvent(DroneEvent.LOCATION_CHANGE);
        List<DroneLog> logs = droneLogRepo.findAll(Example.of(criteria));
        assertThat(logs.size(), is(4));
        assertThat(logs.stream().map(DroneLog::getLogTime).collect(toList()), everyItem(lessThan(LocalDateTime.now())));
        assertThat(logs.stream().map(DroneLog::getNewValue).collect(toList()), everyItem(notNullValue()));
    }

    @Test
    void loadingJob() {
        when(driver.getLoadingPercentage()).thenReturn(100, 0, 50, 100);
        job.loadingLog();

        var criteria = new DroneLog();
        criteria.setEvent(DroneEvent.STATE_CHANGE);
        List<DroneLog> logs = droneLogRepo.findAll(Example.of(criteria));
        assertThat(logs.size(), is(2));
        assertThat(logs.stream().map(DroneLog::getLogTime).collect(toList()), everyItem(lessThan(LocalDateTime.now())));
        assertThat(logs.stream().map(DroneLog::getNewValue).collect(toList()), everyItem(is(DroneState.LOADED.name())));
    }

    private static List<Drone> getTestData() {
        List<Drone> data = new ArrayList<>();
        data.add(DroneBuilder.builder().serial("1").model(DroneModel.LIGHTWEIGHT).state(DroneState.LOADING).batteryLevel(100).build());
        data.add(DroneBuilder.builder().serial("2").model(DroneModel.LIGHTWEIGHT).state(DroneState.LOADING).batteryLevel(100).build());
        data.add(DroneBuilder.builder().serial("3").model(DroneModel.LIGHTWEIGHT).state(DroneState.LOADING).batteryLevel(100).build());
        data.add(DroneBuilder.builder().serial("4").model(DroneModel.LIGHTWEIGHT).state(DroneState.LOADING).batteryLevel(100).build());
        return data;
    }
}
