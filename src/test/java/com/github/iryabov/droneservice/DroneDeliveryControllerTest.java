package com.github.iryabov.droneservice;

import com.github.iryabov.droneservice.entity.*;
import com.github.iryabov.droneservice.exception.DroneDeliveryException;
import com.github.iryabov.droneservice.model.*;
import com.github.iryabov.droneservice.service.DroneService;
import com.github.iryabov.droneservice.service.ShippingService;
import com.github.iryabov.droneservice.model.ResponseId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DroneDeliveryControllerTest {
    @Autowired
    private WebTestClient client;
    @MockBean
    private DroneService droneService;
    @MockBean
    private ShippingService shippingService;


    @Test
    void create() {
        when(droneService.create(any())).thenReturn(1);
        DroneRegistrationForm form = DroneRegistrationForm.builder().serial("test").model(DroneModel.LIGHTWEIGHT).build();
        client.post().uri("/drones")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(form), DroneRegistrationForm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseId.class).isEqualTo(new ResponseId<>(1));
    }

    @Test
    void delete() {
        client.delete().uri("/drones/{id}", 1)
                .exchange()
                .expectStatus().isOk();
        verify(droneService).delete(eq(1));
    }

    @Test
    void getAll() {
        var drone = new DroneBriefInfo();
        drone.setId(1);
        drone.setName("test-drone");
        drone.setState(DroneState.IDLE);

        //get all by state and model
        when(droneService.getAllByStateAndModel(DroneState.IDLE, DroneModel.LIGHTWEIGHT)).thenReturn(List.of(drone));
        client.get().uri("/drones?state={state}&model={model}", "IDLE", "LIGHTWEIGHT")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DroneBriefInfo.class)
                .hasSize(1)
                .contains(drone);
        verify(droneService).getAllByStateAndModel(eq(DroneState.IDLE), eq(DroneModel.LIGHTWEIGHT));

        //get all by model
        when(droneService.getAllByStateAndModel(null, DroneModel.LIGHTWEIGHT)).thenReturn(List.of(drone));
        client.get().uri("/drones?model={model}", "LIGHTWEIGHT")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DroneBriefInfo.class)
                .hasSize(1)
                .contains(drone);
        verify(droneService).getAllByStateAndModel(eq(null), eq(DroneModel.LIGHTWEIGHT));

        //get all by state
        when(droneService.getAllByStateAndModel(DroneState.IDLE, null)).thenReturn(List.of(drone));
        client.get().uri("/drones?state={state}", "IDLE")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DroneBriefInfo.class)
                .hasSize(1)
                .contains(drone);
        verify(droneService).getAllByStateAndModel(eq(DroneState.IDLE), eq(null));
    }

    @Test
    void getOne() {
        var detailedInfo = new DroneDetailedInfo();
        detailedInfo.setId(1);
        detailedInfo.setName("test-drone");
        detailedInfo.setState(DroneState.IDLE);

        when(droneService.getDetailedInfo(1)).thenReturn(detailedInfo);
        client.get().uri("/drones/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DroneDetailedInfo.class)
                .isEqualTo(detailedInfo);
    }

    @Test
    void getAllWithLowBattery() {
        var drone = new DroneBriefInfo();
        drone.setId(1);
        drone.setName("test-drone");
        drone.setBatteryLevel(10);
        drone.setState(DroneState.RETURNING);

        when(droneService.getAllWithLowBattery()).thenReturn(List.of(drone));
        client.get().uri("/drones/low_battery")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DroneBriefInfo.class)
                .hasSize(1)
                .contains(drone);
    }

    @Test
    void getEventLogs() {
        DroneLogInfo log = new DroneLogInfo();
        log.setTime(LocalDateTime.of(2023, 3, 22, 10, 20));
        log.setEvent(DroneEvent.BATTERY_CHANGE);
        log.setOldValue("100");
        log.setNewValue("99");

        when(droneService.getEventLogs(1,
                LocalDateTime.of(2023, 3, 22, 0, 0),
                null,
                DroneEvent.BATTERY_CHANGE)
        ).thenReturn(List.of(log));
        client.get().uri("/drones/{id}/logs?from=2023-03-22T00:00:00&event=BATTERY_CHANGE", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DroneLogInfo.class)
                .hasSize(1)
                .contains(log);
    }

    @Test
    void getAllReadyForLoading() {
        var drone = new DroneBriefInfo();
        drone.setId(1);
        drone.setName("test-drone");
        drone.setState(DroneState.IDLE);

        when(shippingService.getDronesReadyForLoading()).thenReturn(List.of(drone));
        client.get().uri("/drones/ready_for_loading")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DroneBriefInfo.class)
                .hasSize(1)
                .contains(drone);
    }

    @Test
    void getAllReadyForShipping() {
        var drone = new DroneBriefInfo();
        drone.setId(1);
        drone.setName("test-drone");
        drone.setState(DroneState.LOADED);

        when(shippingService.getDronesReadyForShipping()).thenReturn(List.of(drone));
        client.get().uri("/drones/ready_for_shipping")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(DroneBriefInfo.class)
                .hasSize(1)
                .contains(drone);
    }

    @Test
    void load() {
        PackageForm form = PackageForm.builder()
                .items(List.of(
                        new PackageForm.Item(1, 2),
                        new PackageForm.Item(2, 5))).build();

        when(shippingService.load(1, form)).thenReturn(10);
        client.post().uri("/drones/{id}/load", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(form), PackageForm.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseId.class).isEqualTo(new ResponseId<>(10));
    }

    @Test
    void send() {
        DeliveryAddressForm form = DeliveryAddressForm.builder()
                .address("Test address")
                .latitude(-10.5)
                .longitude(10.5).build();
        client.post().uri("/drones/{id}/send", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(form), DeliveryAddressForm.class)
                .exchange()
                .expectStatus().isOk();
        verify(shippingService).send(1, form);
    }

    @Test
    void returnBack() {
        client.post().uri("/drones/{id}/return", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
        verify(shippingService).returnBack(1);
    }

    @Test
    void unload() {
        client.post().uri("/drones/{id}/unload", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
        verify(shippingService).unload(1);
    }

    @Test
    void getShippingInfo() {
        var shipping = new ShippingInfo();
        shipping.setId(1);

        when(shippingService.getShippingInfo(1)).thenReturn(shipping);
        client.get().uri("/drones/shipping/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShippingInfo.class)
                .isEqualTo(shipping);
    }

    @Test
    void trackShipping() {
        var log = new ShippingLogInfo();
        log.setTime(LocalDateTime.now());
        log.setEvent(ShippingEvent.STATUS_CHANGE);
        log.setOldValue(DeliveryStatus.SHIPPED.name());
        log.setNewValue(DeliveryStatus.DELIVERED.name());

        when(shippingService.trackShipment(1)).thenReturn(List.of(log));
        client.get().uri("/drones/shipping/{id}/track", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ShippingLogInfo.class)
                .hasSize(1)
                .contains(log);
    }

    @Test
    void validationsHandling() {
        PackageForm form = PackageForm.builder()
                .items(List.of(new PackageForm.Item(1, -1))).build();

        when(shippingService.load(1, form)).thenThrow(new DroneDeliveryException("Some validation message"));
        var result = client.post().uri("/drones/{id}/load", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(form), PackageForm.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ValidationError.class)
                .returnResult();
        assertThat(result.getStatus(), is(HttpStatusCode.valueOf(400)));
        assertThat(result.getResponseBody(), notNullValue());
        assertThat(result.getResponseBody().getMessage(), is("Some validation message"));
    }
}
