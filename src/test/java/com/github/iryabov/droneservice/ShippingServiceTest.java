package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.entity.*;
import com.github.iryabov.droneservice.model.*;
import com.github.iryabov.droneservice.repository.DroneRepository;
import com.github.iryabov.droneservice.service.ShippingService;
import com.github.iryabov.droneservice.test.DroneBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ShippingServiceTest {
    @Autowired
    private ShippingService service;

    @Autowired
    private DroneRepository droneRepo;

    @BeforeEach
    void setUp() {
        droneRepo.saveAll(testData());
    }

    @Test
    void simpleSuccessfulDelivery() {
        var drones = service.getDronesReadyForLoading();
        assertThat(drones.size(), greaterThan(0));
        assertThat(drones.stream().map(DroneBriefInfo::getState).collect(toList()), everyItem(is(DroneState.IDLE)));

        //loading package
        var someDrone = drones.get(0);
        var shippingId = service.load(someDrone.getId(), PackageForm.builder()
                .items(List.of(
                        new PackageForm.Item(1, 2),
                        new PackageForm.Item(2, 1)))
                .build());
        var shipping = service.getShippingInfo(shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.PENDING));
        assertThat(shipping.getDrone().getState(), is(DroneState.LOADING));

        //checking that the drone loaded
        changeState(someDrone.getId(), DroneState.LOADED);
        drones = service.getDronesReadyForShipping();
        assertThat(drones.stream().map(DroneBriefInfo::getState).collect(toList()), everyItem(is(DroneState.LOADED)));
        assertThat(drones.stream().map(DroneBriefInfo::getId).findAny(), is(Optional.of(someDrone.getId())));

        //sending drone
        service.send(someDrone.getId(), DeliveryAddressForm.builder()
                .address("бул. Драган Цанков 36, София, Болгария")
                .latitude(42.67034)
                .longitude(23.35111)
                .build());
        shipping = service.getShippingInfo(shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.SHIPPED));
        assertThat(shipping.getDrone().getState(), is(DroneState.DELIVERING));

        //delivering
        service.unload(someDrone.getId());
        shipping = service.getShippingInfo(shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.DELIVERED));
        assertThat(shipping.getDrone().getState(), is(DroneState.DELIVERED));

        //returning back
        service.returnBack(someDrone.getId());
        shipping = service.getShippingInfo(shippingId);
        assertThat(shipping.getDrone().getState(), is(DroneState.RETURNING));

        //checking that the drone returned
        changeState(someDrone.getId(), DroneState.IDLE);
        drones = service.getDronesReadyForLoading();
        assertThat(drones.size(), greaterThan(0));
        assertThat(drones.stream().map(DroneBriefInfo::getId).collect(toList()), hasItem(someDrone.getId()));

        //checking logs
        var logs = service.trackShipment(shippingId);
        assertThat(logs.size(), is(3));

        assertThat(logs.get(0).getEvent(), is(ShippingEvent.STATUS_CHANGE));
        assertThat(logs.get(0).getNewValue(), is(DeliveryStatus.PENDING.toString()));

        assertThat(logs.get(1).getEvent(), is(ShippingEvent.STATUS_CHANGE));
        assertThat(logs.get(1).getOldValue(), is(DeliveryStatus.PENDING.toString()));
        assertThat(logs.get(1).getNewValue(), is(DeliveryStatus.SHIPPED.toString()));

        assertThat(logs.get(2).getEvent(), is(ShippingEvent.STATUS_CHANGE));
        assertThat(logs.get(2).getOldValue(), is(DeliveryStatus.SHIPPED.toString()));
        assertThat(logs.get(2).getNewValue(), is(DeliveryStatus.DELIVERED.toString()));
    }

    private void changeState(int droneId, DroneState state) {
        var drone = droneRepo.findById(droneId).orElseThrow();
        drone.setState(state);
        droneRepo.save(drone);
    }

    private List<Drone> testData() {
        List<Drone> drones = new ArrayList<>();
        drones.add(DroneBuilder.builder().id(1).model(DroneModel.LIGHTWEIGHT).state(DroneState.IDLE).batteryLevel(100).build());
        return drones;
    }
}
