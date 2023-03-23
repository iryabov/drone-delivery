package com.github.iryabov.dronedelivery.controller;

import com.github.iryabov.dronedelivery.enums.DroneEvent;
import com.github.iryabov.dronedelivery.enums.DroneModel;
import com.github.iryabov.dronedelivery.enums.DroneState;
import com.github.iryabov.dronedelivery.model.*;
import com.github.iryabov.dronedelivery.service.DroneService;
import com.github.iryabov.dronedelivery.service.ShippingService;
import com.github.iryabov.dronedelivery.model.ResponseId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/drones")
@AllArgsConstructor
public class DroneDeliveryController {
    private DroneService droneService;
    private ShippingService shippingService;

    @Operation(summary = "Register a new drone")
    @ApiResponse(responseCode = "200", description = "Drone was registered")
    @PostMapping
    public ResponseId<Integer> create(@Parameter(description = "Form of drone's registration") @RequestBody DroneRegistrationForm registrationForm) {
        return new ResponseId<>(droneService.create(registrationForm));
    }
    @Operation(summary = "Delete a drone")
    @ApiResponse(responseCode = "200", description = "Drone was deleted")
    @DeleteMapping("/{droneId}")
    public void delete(@Parameter(description = "Identifier of drone") @PathVariable int droneId) {
        droneService.delete(droneId);
    }
    @Operation(summary = "Get detail information about a drone")
    @ApiResponse(responseCode = "200", description = "Detailed information about a drone")
    @GetMapping("/{droneId}")
    public DroneDetailedInfo getDetailedInfo(@Parameter(description = "Identifier of drone") @PathVariable int droneId) {
        return droneService.getDetailedInfo(droneId);
    }
    @Operation(summary = "Get drone's events log")
    @ApiResponse(responseCode = "200", description = "List of log records")
    @GetMapping("/{droneId}/logs")
    public List<DroneLogInfo> getEventLogs(@Parameter(description = "Identifier of drone") @PathVariable int droneId,
                                           @Parameter(description = "Logs of what event you need to find") @RequestParam(name = "event") DroneEvent event,
                                           @Parameter(description = "Date and time from which logs should be found") @RequestParam(name = "from", required = false) LocalDateTime from,
                                           @Parameter(description = "Date and time until which logs should be found") @RequestParam(name = "till", required = false) LocalDateTime till) {
        return droneService.getEventLogs(droneId, from, till, event);
    }
    @Operation(summary = "Get all drones by state and model")
    @ApiResponse(responseCode = "200", description = "List of found drones")
    @GetMapping
    public List<DroneBriefInfo> getAllByStateAndModel(@Parameter(description = "State of drone") @RequestParam(name = "state", required = false) DroneState state,
                                                      @Parameter(description = "Model of drone")  @RequestParam(name = "model", required = false) DroneModel model) {
        return droneService.getAllByStateAndModel(state, model);
    }
    @Operation(summary = "Get list of drones with low battery charge")
    @ApiResponse(responseCode = "200", description = "List of found drones")
    @GetMapping("/low_battery")
    public List<DroneBriefInfo> getAllWithLowBattery() {
        return droneService.getAllWithLowBattery();
    }

    @Operation(summary = "Get list of drones ready for loading")
    @ApiResponse(responseCode = "200", description = "List of found drones")
    @GetMapping("/ready_for_loading")
    public List<DroneBriefInfo> getDronesReadyForLoading() {
        return shippingService.getDronesReadyForLoading();
    }
    @Operation(summary = "Get list of drones ready for shipping")
    @ApiResponse(responseCode = "200", description = "List of found drones")
    @GetMapping("/ready_for_shipping")
    public List<DroneBriefInfo> getDronesReadyForShipping() {
        return shippingService.getDronesReadyForShipping();
    }
    @Operation(summary = "Send a command to the drone to load the package")
    @ApiResponse(responseCode = "200", description = "Created shipping task")
    @PostMapping("/{droneId}/load")
    public ResponseId<Integer> load(@Parameter(description = "Identifier of drone") @PathVariable int droneId,
                                    @Parameter(description = "Form of package")
                                    @RequestBody PackageForm shippingPackage) {
        return new ResponseId<>(shippingService.load(droneId, shippingPackage));
    }
    @Operation(summary = "Send a command to the drone to fly to the delivery address")
    @ApiResponse(responseCode = "200", description = "Drone are flying")
    @PostMapping("/{droneId}/send")
    public void send(@Parameter(description = "Identifier of drone") @PathVariable int droneId,
                     @Parameter(description = "Form of delivery address")
                     @RequestBody DeliveryAddressForm destination) {
        shippingService.send(droneId, destination);
    }
    @Operation(summary = "Send a command to the drone to unload the package")
    @ApiResponse(responseCode = "200", description = "Drone unloaded")
    @PostMapping("/{droneId}/unload")
    public void unload(@Parameter(description = "Identifier of drone") @PathVariable int droneId) {
        shippingService.unload(droneId);
    }
    @Operation(summary = "Send a command to the drone to get back to the warehouse")
    @ApiResponse(responseCode = "200", description = "Drone are coming back")
    @PostMapping("/{droneId}/return")
    public void returnBack(@Parameter(description = "Identifier of drone") @PathVariable int droneId) {
        shippingService.returnBack(droneId);
    }
    @Operation(summary = "Get detail information about the shipping")
    @ApiResponse(responseCode = "200", description = "Detailed information about the shipping")
    @GetMapping("/shipping/{shipmentId}")
    public ShippingInfo getShippingInfo(@Parameter(description = "Identifier of shipment") @PathVariable int shipmentId) {
        return shippingService.getShippingInfo(shipmentId);
    }
    @Operation(summary = "Track shipment")
    @ApiResponse(responseCode = "200", description = "Passed stages of shipping")
    @GetMapping("/shipping/{shipmentId}/track")
    public List<ShippingLogInfo> trackShipment(@Parameter(description = "Identifier of shipment") @PathVariable int shipmentId) {
        return shippingService.trackShipment(shipmentId);
    }
}
