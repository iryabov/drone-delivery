package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.client.DroneClient;
import com.github.iryabov.droneservice.entity.*;
import com.github.iryabov.droneservice.model.*;
import com.github.iryabov.droneservice.repository.DroneRepository;
import com.github.iryabov.droneservice.service.ShippingService;
import com.github.iryabov.droneservice.test.DroneBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ShippingServiceTest {
    @Autowired
    private ShippingService service;
    @Autowired
    private DroneRepository droneRepo;
    @MockBean
    private DroneClient droneClient;
    @Mock
    private DroneClient.Driver driver;

    @BeforeEach
    void setUp() {
        given(this.droneClient.lookup(ArgumentMatchers.anyString())).willReturn(driver);
        droneRepo.saveAll(testData());
    }

    @Test
    void simpleSuccessfulDelivery() {
        //Getting drones available for delivery
        var drones = service.getDronesReadyForLoading();
        assertThat(drones.size(), greaterThan(0));
        assertThat(drones.stream().map(DroneBriefInfo::getState).collect(toList()), everyItem(is(DroneState.IDLE)));

        //Loading package to the drone
        var someDrone = drones.get(0);
        var shippingId = service.load(someDrone.getId(), PackageForm.builder()
                .items(List.of(
                        new PackageForm.Item(1, 2.0),
                        new PackageForm.Item(2, 1.0)))
                .build());
        verify(driver).load(3.0);

        var shipping = service.getShippingInfo(shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.PENDING));
        assertThat(shipping.getDrone().getState(), is(DroneState.LOADING));

        //Checking that the drone loaded
        changeStateForTest(someDrone.getId(), DroneState.LOADED);
        drones = service.getDronesReadyForShipping();
        assertThat(drones.stream().map(DroneBriefInfo::getState).collect(toList()), everyItem(is(DroneState.LOADED)));
        assertThat(drones.stream().map(DroneBriefInfo::getId).findAny(), is(Optional.of(someDrone.getId())));

        //Sending the drone to the delivery address
        service.send(someDrone.getId(), DeliveryAddressForm.builder()
                .address("бул. Драган Цанков 36, София, Болгария")
                .latitude(42.67034)
                .longitude(23.35111)
                .build());
        verify(driver).flyTo(new DroneClient.Point(42.67034, 23.35111));

        shipping = service.getShippingInfo(shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.SHIPPED));
        assertThat(shipping.getDrone().getState(), is(DroneState.DELIVERING));

        //Passing the package to the customer
        service.unload(someDrone.getId());
        verify(driver).unload();

        shipping = service.getShippingInfo(shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.DELIVERED));
        assertThat(shipping.getDrone().getState(), is(DroneState.DELIVERED));

        //Returning the drone back
        service.returnBack(someDrone.getId());
        verify(driver).flyToBase();

        shipping = service.getShippingInfo(shippingId);
        assertThat(shipping.getDrone().getState(), is(DroneState.RETURNING));

        //Checking that the drone returned
        changeStateForTest(someDrone.getId(), DroneState.IDLE);
        drones = service.getDronesReadyForLoading();
        assertThat(drones.size(), greaterThan(0));
        assertThat(drones.stream().map(DroneBriefInfo::getId).collect(toList()), hasItem(someDrone.getId()));

        //Checking delivery logs
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

    private void changeStateForTest(int droneId, DroneState state) {
        var drone = droneRepo.findById(droneId).orElseThrow();
        drone.setState(state);
        droneRepo.save(drone);
    }

    private List<Drone> testData() {
        List<Drone> drones = new ArrayList<>();
        drones.add(DroneBuilder.builder().id(1).serial("01").model(DroneModel.LIGHTWEIGHT).state(DroneState.IDLE).batteryLevel(100).build());
        return drones;
    }
}
