package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.client.DroneClient;
import com.github.iryabov.droneservice.entity.*;
import com.github.iryabov.droneservice.exception.DroneDeliveryException;
import com.github.iryabov.droneservice.model.*;
import com.github.iryabov.droneservice.repository.DroneRepository;
import com.github.iryabov.droneservice.repository.MedicationRepository;
import com.github.iryabov.droneservice.service.ShippingService;
import com.github.iryabov.droneservice.test.DroneBuilder;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ShippingServiceTest {
    @Autowired
    private ShippingService service;
    @Autowired
    private DroneRepository droneRepo;
    @Autowired
    private MedicationRepository medicationRepo;
    @MockBean
    private DroneClient droneClient;
    @Mock
    private DroneClient.Driver driver;

    @BeforeEach
    void setUp() {
        given(this.droneClient.lookup(anyString(), any())).willReturn(driver);
        droneRepo.saveAll(testDroneData());
        medicationRepo.saveAll(testMedicationsData());
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
                        new PackageForm.Item(1, 4),
                        new PackageForm.Item(2, 2)))
                .build());
        verify(driver).load(0.5);

        var shipping = service.getShippingInfo(shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.PENDING));
        assertThat(shipping.getDrone().getState(), is(DroneState.LOADING));
        assertThat(shipping.getPackageInfo().getTotalWeight(), is(0.5));

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

    @Test
    void validationsOnLoad() {
        //Not valid, because the list of goods was not specified when loading
        assertThrows(ConstraintViolationException.class, () -> {
            service.load(1, PackageForm.builder()
                    .items(new ArrayList<>())
                    .build());
        });

        //Not valid, because the quantity of the goods in the package is not set
        assertThrows(ConstraintViolationException.class, () -> {
            service.load(1, PackageForm.builder()
                    .items(List.of(
                            new PackageForm.Item(1, null),
                            new PackageForm.Item(1, 0)))
                    .build());
        });

        //Not valid, because there was an overweight
        assertThrows(DroneDeliveryException.class, () -> {
            service.load(1, PackageForm.builder()
                    .items(List.of(
                            new PackageForm.Item(1, 10),
                            new PackageForm.Item(1, 1)))
                    .build());
        });

        //Not valid, because there was low battery
        assertThrows(DroneDeliveryException.class, () -> {
            var someDrone = droneRepo.save(DroneBuilder.builder().batteryLevel(20).serial("lowbattery").state(DroneState.IDLE).build());
            service.load(someDrone.getId(), PackageForm.builder()
                    .items(List.of(new PackageForm.Item(1, 1)))
                    .build());
        });

        //Not valid, because the drone must be idle before loading
        assertThrows(DroneDeliveryException.class, () -> {
            var someDrone = droneRepo.save(DroneBuilder.builder().state(DroneState.RETURNING).serial("notIdle").build());
            service.load(someDrone.getId(), PackageForm.builder()
                    .items(List.of(new PackageForm.Item(1, 1)))
                    .build());
        });
    }

    @Test
    void validationsOnSend() {
        //Not valid, because the destination coordinates are incorrect
        assertThrows(ConstraintViolationException.class, () -> {
            service.send(1, DeliveryAddressForm.builder()
                    .latitude(-100)
                    .longitude(100)
                    .build());
        });

        //Not valid, because the drone must be loaded before sending
        assertThrows(DroneDeliveryException.class, () -> {
            var someDrone = droneRepo.save(DroneBuilder.builder().state(DroneState.LOADING).serial("notLoaded").build());
            service.send(someDrone.getId(), DeliveryAddressForm.builder()
                    .latitude(10)
                    .longitude(10)
                    .build());
        });
    }

    @Test
    void validationsOnReturning() {
        //Not valid, because the drone must be delivering or delivered before returning
        assertThrows(DroneDeliveryException.class, () -> {
            service.returnBack(1);
        });
    }

    @Test
    void validationsOnUnloading() {
        //Not valid, because the drone must be delivering or loading or loaded before unloading
        assertThrows(DroneDeliveryException.class, () -> {
            service.unload(1);
        });
    }

    private void changeStateForTest(int droneId, DroneState state) {
        var drone = droneRepo.findById(droneId).orElseThrow();
        drone.setState(state);
        droneRepo.save(drone);
    }

    private List<Drone> testDroneData() {
        List<Drone> drones = new ArrayList<>();
        drones.add(DroneBuilder.builder().id(1).serial("01").model(DroneModel.LIGHTWEIGHT).state(DroneState.IDLE).batteryLevel(100).build());
        return drones;
    }

    private List<Medication> testMedicationsData() {
        List<Medication> medications = new ArrayList<>();
        var pen = new Medication();
        pen.setId(1);
        pen.setName("Penicillins");
        pen.setCode("PEN");
        pen.setWeight(0.05);
        medications.add(pen);

        var pan = new Medication();
        pan.setId(2);
        pan.setName("Panadol");
        pan.setCode("PAN");
        pan.setWeight(0.15);
        medications.add(pan);
        return medications;
    }
}
