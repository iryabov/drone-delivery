package com.github.iryabov.droneservice.controller;

import com.github.iryabov.droneservice.entity.DroneEvent;
import com.github.iryabov.droneservice.entity.DroneModel;
import com.github.iryabov.droneservice.entity.DroneState;
import com.github.iryabov.droneservice.model.*;
import com.github.iryabov.droneservice.service.DroneService;
import com.github.iryabov.droneservice.service.ShippingService;
import com.github.iryabov.droneservice.model.ResponseId;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/drones")
@AllArgsConstructor
public class DroneDeliveryController {
    private DroneService droneService;
    private ShippingService shippingService;

    @PostMapping
    public ResponseId<Integer> create(@RequestBody DroneRegistrationForm registrationForm) {
        return new ResponseId<>(droneService.create(registrationForm));
    }
    @DeleteMapping("/{droneId}")
    public void delete(@PathVariable int droneId) {
        droneService.delete(droneId);
    }
    @GetMapping("/{droneId}")
    public DroneDetailedInfo getDetailedInfo(@PathVariable int droneId) {
        return droneService.getDetailedInfo(droneId);
    }
    @GetMapping("/{droneId}/logs")
    public List<DroneLogInfo> getEventLogs(@PathVariable int droneId,
                                           @RequestParam(name = "from") LocalDateTime from,
                                           @RequestParam(name = "till") LocalDateTime till,
                                           @RequestParam(name = "event", required = false) DroneEvent event) {
        return droneService.getEventLogs(droneId, from, till, event);
    }
    @GetMapping
    public List<DroneBriefInfo> getAllByStateAndModel(@RequestParam(name = "state", required = false) DroneState state,
                                                      @RequestParam(name = "model", required = false) DroneModel model) {
        return droneService.getAllByStateAndModel(state, model);
    }
    @GetMapping("/low_battery")
    public List<DroneBriefInfo> getAllWithLowBattery() {
        return droneService.getAllWithLowBattery();
    }

    @GetMapping("/ready_for_loading")
    public List<DroneBriefInfo> getDronesReadyForLoading() {
        return shippingService.getDronesReadyForLoading();
    }
    @GetMapping("/ready_for_shipping")
    public List<DroneBriefInfo> getDronesReadyForShipping() {
        return shippingService.getDronesReadyForShipping();
    }
    @PostMapping("/{droneId}/load")
    public ResponseId<Integer> load(@PathVariable int droneId, @RequestBody PackageForm shippingPackage) {
        return new ResponseId<>(shippingService.load(droneId, shippingPackage));
    }
    @PostMapping("/{droneId}/send")
    public void send(@PathVariable int droneId, @RequestBody DeliveryAddressForm destination) {
        shippingService.send(droneId, destination);
    }
    @PostMapping("/{droneId}/return")
    public void returnBack(@PathVariable int droneId) {
        shippingService.returnBack(droneId);
    }
    @PostMapping("/{droneId}/unload")
    public void unload(@PathVariable int droneId) {
        shippingService.unload(droneId);
    }
    @GetMapping("/shipping/{shipmentId}")
    public ShippingInfo getShippingInfo(@PathVariable int shipmentId) {
        return shippingService.getShippingInfo(shipmentId);
    }
    @GetMapping("/shipping/{shipmentId}/track")
    public List<ShippingLogInfo> trackShipment(@PathVariable int shipmentId) {
        return shippingService.trackShipment(shipmentId);
    }
}
