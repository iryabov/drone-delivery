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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        Shipping shipping = new Shipping();
        shipping.setDrone(drone);
        shipping.setStatus(DeliveryStatus.PENDING);
        shipping.setItems(shippingMapper.toPackageItems(shippingPackage, shipping));
        shipping.setDestination(new Location());
        Shipping createdShipping = shippingRepo.save(shipping);

        drone.setShipping(createdShipping);
        drone.setState(DroneState.LOADING);
        droneRepo.save(drone);

        trackDroneState(droneId, DroneState.LOADING);
        trackDeliveryStatus(createdShipping.getId(), DeliveryStatus.PENDING);

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.load(totalWeight);
        return createdShipping.getId();
    }

    @Override
    public void send(int droneId, DeliveryAddressForm destination) {
        validate(validator, destination);
        validateRequiringState(droneId, DroneState.LOADED);
        validateLowBattery(droneId);

        Drone drone = droneRepo.findById(droneId).orElseThrow();
        Shipping shipping = drone.getShipping();
        shipping.setDestination(new Location(destination.getLatitude(), destination.getLongitude()));
        shipping.setDeliveryAddress(destination.getAddress());
        shipping.setStatus(DeliveryStatus.SHIPPED);
        shippingRepo.save(shipping);

        drone.setState(DroneState.DELIVERING);
        droneRepo.save(drone);

        trackDroneState(droneId, drone.getState());
        trackDeliveryStatus(shipping.getId(), shipping.getStatus());

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.flyTo(new DroneClient.Point(destination.getLatitude(), destination.getLongitude()));
    }

    @Override
    public void unload(int droneId) {
        validateRequiringState(droneId,
                DroneState.LOADING, DroneState.LOADED,
                DroneState.DELIVERING, DroneState.ARRIVED);

        Drone drone = droneRepo.findById(droneId).orElseThrow();
        Shipping shipping = drone.getShipping();

        switch (drone.getState()) {
            case LOADING, LOADED -> {
                drone.setState(DroneState.IDLE);
                shipping.setStatus(DeliveryStatus.CANCELED);
            }
            case DELIVERING, ARRIVED -> {
                drone.setState(DroneState.DELIVERED);
                shipping.setStatus(DeliveryStatus.DELIVERED);
            }
        }
        shippingRepo.save(shipping);

        drone.setShipping(null);
        droneRepo.save(drone);

        trackDroneState(droneId, drone.getState());
        trackDeliveryStatus(shipping.getId(), shipping.getStatus());

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.unload();
    }

    @Override
    public void returnBack(int droneId) {
        validateRequiringState(droneId, DroneState.DELIVERING, DroneState.ARRIVED, DroneState.DELIVERED);

        Drone drone = droneRepo.findById(droneId).orElseThrow();
        Shipping shipping = drone.getShipping();
        if (shipping != null) {
            shipping.setStatus(DeliveryStatus.CANCELED);
            shippingRepo.save(shipping);
            trackDeliveryStatus(shipping.getId(), shipping.getStatus());
        }

        drone.setState(DroneState.RETURNING);
        droneRepo.save(drone);

        trackDroneState(droneId, DroneState.RETURNING);

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.flyToBase();
    }

    @Override
    public List<ShippingBriefInfo> getDroneDeliveries(int droneId,
                                                      DeliveryStatus status,
                                                      Integer page,
                                                      Integer size) {
        Shipping criteria = new Shipping();
        if (status != null)
            criteria.setStatus(status);
        Page<Shipping> shippingPage = shippingRepo.findAll(Example.of(criteria),
                PageRequest.of(
                        page != null ? page : 0,
                        size != null ? size : 10));
        return shippingPage.map(s -> shippingMapper.toShippingBriefInfo(s, new ShippingBriefInfo())).toList();
    }

    @Override
    public ShippingDetailedInfo getShippingDetailedInfo(int droneId, int shipmentId) {
        Shipping shipping = shippingRepo.findByDroneIdAndId(droneId, shipmentId).orElseThrow();
        ShippingDetailedInfo shippingDetailedInfo = shippingMapper.toShippingBriefInfo(shipping, new ShippingDetailedInfo());
        shippingDetailedInfo.setDrone(droneMapper.toBriefInfo(shipping.getDrone()));
        shippingDetailedInfo.setPackageInfo(shippingMapper.toPackageInfo(shipping.getItems()));
        return shippingDetailedInfo;
    }

    @Override
    public List<ShippingLogInfo> trackShipment(int shipmentId) {
        List<ShippingLog> logs = shippingLogRepo.findAllByShippingId(shipmentId);
        List<ShippingLogInfo> logsInfo = logs.stream().map(shippingMapper::toLogInfo).collect(toList());
        for (int i = 1; i < logsInfo.size(); i++) {
            logsInfo.get(i).setOldValue(logsInfo.get(i - 1).getNewValue());
        }
        return logsInfo;
    }

    private void trackDeliveryStatus(int shippingId, DeliveryStatus newStatus) {
        ShippingLog entity = new ShippingLog();
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
