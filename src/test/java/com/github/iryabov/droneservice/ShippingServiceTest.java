package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.entity.DeliveryStatus;
import com.github.iryabov.droneservice.entity.DroneState;
import com.github.iryabov.droneservice.entity.ShippingEvent;
import com.github.iryabov.droneservice.model.*;
import com.github.iryabov.droneservice.service.ShippingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ShippingServiceTest {
    @Autowired
    private ShippingService service;

    @Test
    void simpleSuccessfulDelivery() {
        var drones = service.getDronesReadyForLoading();
        assertThat(drones.size(), greaterThan(0));
        assertThat(drones.stream().map(DroneBriefInfo::getState).collect(Collectors.toList()), everyItem(is(DroneState.IDLE)));

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
        drones = service.getDronesReadyForShipping();
        assertThat(drones.stream().map(DroneBriefInfo::getState).collect(Collectors.toList()), everyItem(is(DroneState.LOADED)));
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
        drones = service.getDronesReadyForLoading();
        assertThat(drones.size(), greaterThan(0));
        assertThat(drones.stream().map(DroneBriefInfo::getId).findAny(), is(Optional.of(someDrone.getId())));

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
}