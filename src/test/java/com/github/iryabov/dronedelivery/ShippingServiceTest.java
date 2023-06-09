package com.github.iryabov.dronedelivery;

import com.github.iryabov.dronedelivery.client.DroneClient;
import com.github.iryabov.dronedelivery.entity.*;
import com.github.iryabov.dronedelivery.enums.DeliveryStatus;
import com.github.iryabov.dronedelivery.enums.DroneModel;
import com.github.iryabov.dronedelivery.enums.DroneState;
import com.github.iryabov.dronedelivery.enums.ShippingEvent;
import com.github.iryabov.dronedelivery.exception.DroneDeliveryException;
import com.github.iryabov.dronedelivery.model.*;
import com.github.iryabov.dronedelivery.repository.DroneRepository;
import com.github.iryabov.dronedelivery.repository.MedicationRepository;
import com.github.iryabov.dronedelivery.repository.ShippingRepository;
import com.github.iryabov.dronedelivery.service.ShippingService;
import com.github.iryabov.dronedelivery.test.DroneBuilder;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
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
    @Autowired
    private ShippingRepository shippingRepo;
    @MockBean
    private DroneClient droneClient;
    @Mock
    private DroneClient.Driver driver;

    private int testDroneId, testMedId1, testMedId2;

    @BeforeEach
    void setUp() {
        given(this.droneClient.lookup(anyString(), any())).willReturn(driver);
        testDroneId = droneRepo.save(testDroneData()).getId();
        shippingRepo.saveAll(testShippingData());
        var medications = medicationRepo.saveAll(testMedicationsData());
        testMedId1 = medications.get(0).getId();
        testMedId2 = medications.get(1).getId();
    }

    @AfterEach
    void tearDown() {
        droneRepo.deleteAll();
        shippingRepo.deleteAll();
        medicationRepo.deleteAll();
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
                        new PackageForm.Item(testMedId1, 4),
                        new PackageForm.Item(testMedId2, 2)))
                .build());
        verify(driver).load(0.5);

        var shipping = service.getShippingDetailedInfo(someDrone.getId(), shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.PENDING));
        assertThat(shipping.getPackageInfo().getTotalWeight(), is(0.5));
        var drone = droneRepo.findById(someDrone.getId()).orElseThrow();
        assertThat(drone.getState(), is(DroneState.LOADING));

        //Checking that the drone loaded
        waitingForStateChange(someDrone.getId(), DroneState.LOADED);
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

        shipping = service.getShippingDetailedInfo(someDrone.getId(), shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.SHIPPED));
        drone = droneRepo.findById(someDrone.getId()).orElseThrow();
        assertThat(drone.getState(), is(DroneState.DELIVERING));

        //Passing the package to the customer
        waitingForStateChange(someDrone.getId(), DroneState.ARRIVED);
        service.unload(someDrone.getId());
        verify(driver).unload();

        shipping = service.getShippingDetailedInfo(someDrone.getId(), shippingId);
        assertThat(shipping.getDeliveryStatus(), is(DeliveryStatus.DELIVERED));
        drone = droneRepo.findById(someDrone.getId()).orElseThrow();
        assertThat(drone.getState(), is(DroneState.DELIVERED));
        assertThat(drone.getShipping(), nullValue());

        //Returning the drone back
        service.returnBack(someDrone.getId());
        verify(driver).flyToBase();

        drone = droneRepo.findById(someDrone.getId()).orElseThrow();
        assertThat(drone.getState(), is(DroneState.RETURNING));

        //Checking that the drone returned
        waitingForStateChange(someDrone.getId(), DroneState.IDLE);
        drones = service.getDronesReadyForLoading();
        assertThat(drones.size(), greaterThan(0));
        assertThat(drones.stream().map(DroneBriefInfo::getId).collect(toList()), hasItem(someDrone.getId()));

        //Checking delivery logs
        var logs = service.trackShipment(someDrone.getId(), shippingId);
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
    void readShipping() {
        var list = service.getDroneDeliveries(testDroneId, null, null, null);
        assertThat(list.stream().map(ShippingBriefInfo::getDeliveryStatus).collect(toList()), hasItems(
                DeliveryStatus.PENDING,
                DeliveryStatus.SHIPPED,
                DeliveryStatus.DELIVERED,
                DeliveryStatus.CANCELED));

        list = service.getDroneDeliveries(testDroneId, DeliveryStatus.DELIVERED, null, null);
        assertThat(list.stream().map(ShippingBriefInfo::getDeliveryStatus).collect(toList()), everyItem(is(
                DeliveryStatus.DELIVERED)));

        list = service.getDroneDeliveries(testDroneId, null, 0, 2);
        assertThat(list.size(), not(greaterThan(2)));
    }

    @Test
    void validationsOnLoad() {
        //Not valid, because the list of goods was not specified when loading
        assertThrows(ConstraintViolationException.class, () -> {
            service.load(testDroneId, PackageForm.builder()
                    .items(new ArrayList<>())
                    .build());
        });

        //Not valid, because the quantity of the goods in the package is not set
        assertThrows(ConstraintViolationException.class, () -> {
            service.load(testDroneId, PackageForm.builder()
                    .items(List.of(
                            new PackageForm.Item(1, null),
                            new PackageForm.Item(1, 0)))
                    .build());
        });

        //Not valid, because there was an overweight
        assertThrows(DroneDeliveryException.class, () -> {
            service.load(testDroneId, PackageForm.builder()
                    .items(List.of(
                            new PackageForm.Item(testMedId1, 10),
                            new PackageForm.Item(testMedId2, 5)))
                    .build());
        });

        //Not valid, because there was low battery
        assertThrows(DroneDeliveryException.class, () -> {
            var someDrone = droneRepo.save(DroneBuilder.builder().batteryLevel(20).serial("lowbattery").state(DroneState.IDLE).build());
            service.load(someDrone.getId(), PackageForm.builder()
                    .items(List.of(new PackageForm.Item(testMedId1, 1)))
                    .build());
        });

        //Not valid, because the drone must be idle before loading
        assertThrows(DroneDeliveryException.class, () -> {
            var someDrone = droneRepo.save(DroneBuilder.builder().state(DroneState.RETURNING).serial("notIdle").build());
            service.load(someDrone.getId(), PackageForm.builder()
                    .items(List.of(new PackageForm.Item(testMedId1, 1)))
                    .build());
        });
    }

    @Test
    void validationsOnSend() {
        //Not valid, because the destination coordinates are incorrect
        assertThrows(ConstraintViolationException.class, () -> {
            service.send(testDroneId, DeliveryAddressForm.builder()
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
            service.returnBack(testDroneId);
        });
    }

    @Test
    void validationsOnUnloading() {
        //Not valid, because the drone must be delivering or loading or loaded before unloading
        assertThrows(DroneDeliveryException.class, () -> {
            service.unload(testDroneId);
        });
    }

    private void waitingForStateChange(int droneId, DroneState state) {
        var drone = droneRepo.findById(droneId).orElseThrow();
        drone.setState(state);
        droneRepo.save(drone);
    }

    private Drone testDroneData() {
        return DroneBuilder.builder().serial("shipping").model(DroneModel.LIGHTWEIGHT).state(DroneState.IDLE).batteryLevel(100).build();
    }

    private List<Medication> testMedicationsData() {
        List<Medication> medications = new ArrayList<>();
        var pen = new Medication();
        pen.setName("Penicillins");
        pen.setCode("PEN");
        pen.setWeight(0.05);
        medications.add(pen);

        var pan = new Medication();
        pan.setName("Panadol");
        pan.setCode("PAN");
        pan.setWeight(0.15);
        medications.add(pan);
        return medications;
    }

    private List<Shipping> testShippingData() {
        Drone drone = new Drone();
        drone.setId(testDroneId);
        List<Shipping> list = new ArrayList<>();

        var shipping = new Shipping();
        shipping.setStatus(DeliveryStatus.PENDING);
        shipping.setDrone(drone);
        shipping.setDestination(new Location(1.0,-1.0));
        shipping.setDeliveryAddress("Test address 1");
        list.add(shipping);

        shipping = new Shipping();
        shipping.setStatus(DeliveryStatus.SHIPPED);
        shipping.setDrone(drone);
        shipping.setDestination(new Location(-1.0,1.0));
        shipping.setDeliveryAddress("Test address 2");
        list.add(shipping);

        shipping = new Shipping();
        shipping.setStatus(DeliveryStatus.DELIVERED);
        shipping.setDrone(drone);
        shipping.setDestination(new Location(1.0,1.0));
        shipping.setDeliveryAddress("Test address 3");
        list.add(shipping);

        shipping = new Shipping();
        shipping.setStatus(DeliveryStatus.CANCELED);
        shipping.setDrone(drone);
        shipping.setDestination(new Location(-1.0,-1.0));
        shipping.setDeliveryAddress("Test address 4");
        list.add(shipping);
        return list;
    }
}
