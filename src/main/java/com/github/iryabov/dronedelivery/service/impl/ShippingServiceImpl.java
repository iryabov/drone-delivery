package com.github.iryabov.dronedelivery.service.impl;

import com.github.iryabov.dronedelivery.client.DroneClient;
import com.github.iryabov.dronedelivery.entity.*;
import com.github.iryabov.dronedelivery.enums.DeliveryStatus;
import com.github.iryabov.dronedelivery.enums.DroneEvent;
import com.github.iryabov.dronedelivery.enums.DroneState;
import com.github.iryabov.dronedelivery.enums.ShippingEvent;
import com.github.iryabov.dronedelivery.exception.DroneDeliveryException;
import com.github.iryabov.dronedelivery.mapper.DroneMapper;
import com.github.iryabov.dronedelivery.mapper.ShippingMapper;
import com.github.iryabov.dronedelivery.model.*;
import com.github.iryabov.dronedelivery.repository.*;
import com.github.iryabov.dronedelivery.service.ShippingService;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.iryabov.dronedelivery.validation.ValidateUtil.validate;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional
@AllArgsConstructor
public class ShippingServiceImpl implements ShippingService {

    private DroneClient droneClient;
    private DroneRepository droneRepo;
    private ShippingRepository shippingRepo;
    private ShippingLogRepository shippingLogRepo;
    private DroneLogRepository droneLogRepo;
    private MedicationRepository medicationRepo;
    private DroneMapper droneMapper;
    private ShippingMapper shippingMapper;
    private Validator validator;

    @Override
    public List<DroneBriefInfo> getDronesReadyForLoading() {
        List<Drone> drones = droneRepo.findAllByStateAndBatteryLevelGreaterThan(DroneState.IDLE, 24);
        return drones.stream().map(droneMapper::toBriefInfo).collect(toList());
    }

    @Override
    public List<DroneBriefInfo> getDronesReadyForShipping() {
        List<Drone> drones = droneRepo.findAllByStateAndBatteryLevelGreaterThan(DroneState.LOADED, 24);
        return drones.stream().map(droneMapper::toBriefInfo).collect(toList());
    }

    @Override
    public int load(int droneId, PackageForm shippingPackage) {
        validate(validator, shippingPackage);
        validateRequiringState(droneId, DroneState.IDLE);
        validateLowBattery(droneId);
        double totalWeight = calcTotalWeight(shippingPackage);
        validatePackageWeight(droneId, totalWeight);

        Drone drone = droneRepo.findById(droneId).orElseThrow();
        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.load(totalWeight);

        drone.setState(DroneState.LOADING);
        Shipping shipping = new Shipping();
        shipping.setStatus(DeliveryStatus.PENDING);
        shipping.setItems(shippingMapper.toPackageItems(shippingPackage, shipping));
        shipping.setDestination(new Location());
        Shipping createdShipping = shippingRepo.save(shipping);

        int shippingId = createdShipping.getId();
        drone.setShipping(createdShipping);
        droneRepo.save(drone);

        trackDroneState(droneId, DroneState.LOADING);
        trackDeliveryStatus(shippingId, droneId, DeliveryStatus.PENDING);
        return shippingId;
    }

    @Override
    public void send(int droneId, DeliveryAddressForm destination) {
        validate(validator, destination);
        validateRequiringState(droneId, DroneState.LOADED);
        validateLowBattery(droneId);

        Drone drone = droneRepo.findById(droneId).orElseThrow();
        drone.setState(DroneState.DELIVERING);
        drone.getShipping().setDestination(new Location(destination.getLatitude(), destination.getLongitude()));
        drone.getShipping().setDeliveryAddress(destination.getAddress());
        drone.getShipping().setStatus(DeliveryStatus.SHIPPED);
        droneRepo.save(drone);

        trackDroneState(droneId, DroneState.DELIVERING);
        trackDeliveryStatus(drone.getShipping().getId(), droneId, DeliveryStatus.SHIPPED);

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.flyTo(new DroneClient.Point(destination.getLatitude(), destination.getLongitude()));
    }

    @Override
    public void returnBack(int droneId) {
        validateRequiringState(droneId, DroneState.DELIVERING, DroneState.DELIVERED);

        Drone drone = droneRepo.findById(droneId).orElseThrow();
        drone.setState(DroneState.RETURNING);
        if (drone.getShipping() != null && drone.getShipping().getStatus() == DeliveryStatus.SHIPPED) {
            drone.getShipping().setStatus(DeliveryStatus.CANCELED);

            trackDeliveryStatus(drone.getShipping().getId(), droneId, DeliveryStatus.CANCELED);
        }
        trackDroneState(droneId, DroneState.RETURNING);
        droneRepo.save(drone);

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.flyToBase();
    }

    @Override
    public void unload(int droneId) {
        validateRequiringState(droneId, DroneState.LOADING, DroneState.LOADED, DroneState.DELIVERING);

        Drone drone = droneRepo.findById(droneId).orElseThrow();
        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.unload();

        switch (drone.getState()) {
            case DELIVERING -> {
                drone.setState(DroneState.DELIVERED);
                drone.getShipping().setStatus(DeliveryStatus.DELIVERED);
                trackDroneState(droneId, DroneState.DELIVERED);
                trackDeliveryStatus(drone.getShipping().getId(), droneId, DeliveryStatus.DELIVERED);
            }
            case LOADED, LOADING -> {
                drone.setState(DroneState.IDLE);
                drone.getShipping().setStatus(DeliveryStatus.PENDING);
                trackDroneState(droneId, DroneState.IDLE);
            }
        }
        droneRepo.save(drone);
    }

    @Override
    public ShippingInfo getShippingInfo(int shipmentId) {
        Shipping shipping = shippingRepo.findById(shipmentId).orElseThrow();
        ShippingInfo shippingInfo = shippingMapper.toInfo(shipping);
        shippingInfo.setDrone(droneMapper.toBriefInfo(shipping.getDrone()));
        shippingInfo.setPackageInfo(shippingMapper.toPackageInfo(shipping.getItems()));
        return shippingInfo;
    }

    @Override
    public List<ShippingLogInfo> trackShipment(int shipmentId) {
        List<ShippingLog> logs = shippingLogRepo.findAllByShippingId(shipmentId);
        List<ShippingLogInfo> logsInfo = logs.stream().map(shippingMapper::toInfo).collect(toList());
        for (int i = 1; i < logsInfo.size(); i++) {
            logsInfo.get(i).setOldValue(logsInfo.get(i - 1).getNewValue());
        }
        return logsInfo;
    }

    private void trackDeliveryStatus(int shippingId, int droneId, DeliveryStatus newStatus) {
        ShippingLog entity = new ShippingLog();
        entity.setDrone(droneRepo.getReferenceById(droneId));
        entity.setShipping(shippingRepo.getReferenceById(shippingId));
        entity.setEvent(ShippingEvent.STATUS_CHANGE);
        entity.setNewValue(newStatus.name());
        entity.setLogTime(LocalDateTime.now());
        shippingLogRepo.save(entity);
    }

    private void trackDroneState(int droneId, DroneState newState) {
        DroneLog entity = new DroneLog();
        entity.setDrone(droneRepo.getReferenceById(droneId));
        entity.setLogTime(LocalDateTime.now());
        entity.setEvent(DroneEvent.STATE_CHANGE);
        entity.setNewValue(newState.name());
        droneLogRepo.save(entity);
    }

    private double calcTotalWeight(PackageForm shippingPackage) {
        Set<Integer> goodsIds = shippingPackage.getItems().stream().map(PackageForm.Item::getGoodsId).collect(Collectors.toSet());
        Map<Integer, Double> weights = medicationRepo.findAllById(goodsIds).stream().collect(toMap(Medication::getId, Medication::getWeight));
        if (weights.size() < goodsIds.size())
            throw new NoSuchElementException();
        return shippingPackage.getItems().stream().map(i -> i.getQuantity() * weights.get(i.getGoodsId())).reduce(0.0, Double::sum);
    }

    private void validatePackageWeight(int droneId, double packageWeight) {
        Drone drone = droneRepo.findById(droneId).orElseThrow();
        double weightCapacity = drone.getModel().getWeightCapacity();
        if (weightCapacity < packageWeight)
            throw new DroneDeliveryException(String.format("Weight exceeded by %d grams. " +
                    "Please choose a more lifting drone", Math.round((packageWeight - weightCapacity) * 1000)));
    }

    private void validateLowBattery(int droneId) {
        Drone drone = droneRepo.findById(droneId).orElseThrow();
        if (drone.getBatteryLevel() < 25)
            throw new DroneDeliveryException("Battery too low. " +
                    "Please select another drone or wait until the battery is charged");
    }

    private void validateRequiringState(int droneId, DroneState... mustBe) {
        Drone drone = droneRepo.findById(droneId).orElseThrow();
        DroneState curState = drone.getState();
        if (!Arrays.asList(mustBe).contains(curState)) {
            String mustBeStates = Arrays.stream(mustBe).map(DroneState::name).reduce((a, b) -> a + " or " + b).orElse("");
            throw new DroneDeliveryException(String.format("Drone must be in state %s, but now it's in %s. " +
                    "Please select another drone", mustBeStates, curState.name()));
        }
    }
}
