package com.github.iryabov.droneservice.service.impl;

import com.github.iryabov.droneservice.client.DroneClient;
import com.github.iryabov.droneservice.entity.*;
import com.github.iryabov.droneservice.mapper.DroneMapper;
import com.github.iryabov.droneservice.mapper.ShippingMapper;
import com.github.iryabov.droneservice.model.*;
import com.github.iryabov.droneservice.repository.DroneRepository;
import com.github.iryabov.droneservice.repository.ShippingLogRepository;
import com.github.iryabov.droneservice.repository.ShippingRepository;
import com.github.iryabov.droneservice.service.ShippingService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@AllArgsConstructor
public class ShippingServiceImpl implements ShippingService {

    private DroneClient droneClient;
    private DroneRepository droneRepo;
    private ShippingRepository shippingRepo;
    private ShippingLogRepository shippingLogRepo;
    private DroneMapper droneMapper;
    private ShippingMapper shippingMapper;

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
        Drone drone = droneRepo.findById(droneId).orElseThrow();

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.load(calcTotalWeight(shippingPackage));

        drone.setState(DroneState.LOADING);
        Shipping shipping = new Shipping();
        shipping.setStatus(DeliveryStatus.PENDING);
        shipping.setItems(shippingMapper.toPackageItems(shippingPackage));
        shipping.setDestination(new Location());
        Shipping createdShipping = shippingRepo.save(shipping);
        int shippingId = createdShipping.getId();
        drone.setShipping(createdShipping);
        droneRepo.save(drone);

        trackLog(shippingId, droneId, DeliveryStatus.PENDING);
        return shippingId;
    }

    @Override
    public void send(int droneId, DeliveryAddressForm destination) {
        Drone drone = droneRepo.findById(droneId).orElseThrow();

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.flyTo(new DroneClient.Point(destination.getLatitude(), destination.getLongitude()));

        drone.setState(DroneState.DELIVERING);
        drone.getShipping().setStatus(DeliveryStatus.SHIPPED);
        droneRepo.save(drone);

        trackLog(drone.getShipping().getId(), droneId, DeliveryStatus.SHIPPED);
    }

    @Override
    public void returnBack(int droneId) {
        Drone drone = droneRepo.findById(droneId).orElseThrow();

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.flyToBase();

        drone.setState(DroneState.RETURNING);
        if (drone.getShipping().getStatus() == DeliveryStatus.SHIPPED) {
            drone.getShipping().setStatus(DeliveryStatus.CANCELED);

            trackLog(drone.getShipping().getId(), droneId, DeliveryStatus.CANCELED);
        }
        droneRepo.save(drone);
    }

    @Override
    public void unload(int droneId) {
        Drone drone = droneRepo.findById(droneId).orElseThrow();

        DroneClient.Driver driver = droneClient.lookup(drone.getSerial(), drone.getModel());
        driver.unload();

        switch (drone.getState()) {
            case DELIVERING -> {
                drone.setState(DroneState.DELIVERED);
                drone.getShipping().setStatus(DeliveryStatus.DELIVERED);
                trackLog(drone.getShipping().getId(), droneId, DeliveryStatus.DELIVERED);
            }
            case LOADED, LOADING -> {
                drone.setState(DroneState.IDLE);
                drone.getShipping().setStatus(DeliveryStatus.PENDING);
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

    private void trackLog(int shippingId, int droneId, DeliveryStatus status) {
        ShippingLog entity = new ShippingLog();
        entity.setDrone(droneRepo.getReferenceById(droneId));
        entity.setShipping(shippingRepo.getReferenceById(shippingId));
        entity.setEvent(ShippingEvent.STATUS_CHANGE);
        entity.setNewValue(status.name());
        shippingLogRepo.save(entity);
    }

    private double calcTotalWeight(PackageForm shippingPackage) {
        return shippingPackage.getItems().stream().map(PackageForm.Item::getAmount).reduce(0.0, Double::sum);
    }
}
