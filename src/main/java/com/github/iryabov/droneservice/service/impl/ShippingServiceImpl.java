package com.github.iryabov.droneservice.service.impl;

import com.github.iryabov.droneservice.entity.*;
import com.github.iryabov.droneservice.model.*;
import com.github.iryabov.droneservice.service.ShippingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShippingServiceImpl implements ShippingService {

    private Drone stubDrone;
    private Shipping stubShipping;
    private List<ShippingLog> stubShippingLogs;

    public ShippingServiceImpl() {
        stubDrone = new Drone();
        stubDrone.setId(1);
        stubDrone.setModel(DroneModel.LIGHTWEIGHT);
        stubDrone.setBatteryLevel(85);
        stubDrone.setWeightLimit(1.5);
        stubDrone.setState(DroneState.IDLE);

        stubShipping = new Shipping();
        stubShipping.setId(1);
        stubShipping.setStatus(DeliveryStatus.PENDING);

        stubShippingLogs = new ArrayList<>();
    }

    @Override
    public List<DroneBriefInfo> getDronesReadyForLoading() {
        if (stubDrone.getState() == DroneState.RETURNING)
            stubDrone.setState(DroneState.IDLE);
        if (stubDrone.getState() == DroneState.IDLE) {
            DroneBriefInfo drone = new DroneBriefInfo();
            drone.setId(stubDrone.getId());
            drone.setState(stubDrone.getState());
            return List.of(drone);
        } else
            return Collections.emptyList();
    }

    @Override
    public List<DroneBriefInfo> getDronesReadyForShipping() {
        if (stubDrone.getState() == DroneState.LOADING)
            stubDrone.setState(DroneState.LOADED);

        if (stubDrone.getState() == DroneState.LOADED) {
            DroneBriefInfo drone = new DroneBriefInfo();
            drone.setId(stubDrone.getId());
            drone.setState(stubDrone.getState());
            return List.of(drone);
        } else
            return Collections.emptyList();
    }

    @Override
    public int load(int droneId, PackageForm shippingPackage) {
        stubDrone.setState(DroneState.LOADING);
        stubDrone.setShipping(stubShipping);
        stubShipping.setStatus(DeliveryStatus.PENDING);
        trackLog(DeliveryStatus.PENDING);
        return stubShipping.getId();
    }

    @Override
    public void send(int droneId, DeliveryAddressForm destination) {
        stubDrone.setState(DroneState.DELIVERING);
        stubShipping.setStatus(DeliveryStatus.SHIPPED);
        trackLog(DeliveryStatus.SHIPPED);
    }

    @Override
    public void returnBack(int droneId) {
        stubDrone.setState(DroneState.RETURNING);
        if (stubShipping.getStatus() == DeliveryStatus.SHIPPED) {
            stubShipping.setStatus(DeliveryStatus.CANCELED);
            trackLog(DeliveryStatus.CANCELED);
        }
    }

    @Override
    public void unload(int droneId) {
        switch (stubDrone.getState()) {
            case DELIVERING -> {
                stubDrone.setState(DroneState.DELIVERED);
                stubShipping.setStatus(DeliveryStatus.DELIVERED);
                trackLog(DeliveryStatus.DELIVERED);
            }
            case LOADED, LOADING -> {
                stubDrone.setState(DroneState.IDLE);
                stubShipping.setStatus(DeliveryStatus.PENDING);
            }
        }
    }

    @Override
    public ShippingInfo getShippingInfo(int shipmentId) {
        ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setId(stubShipping.getId());
        shippingInfo.setDeliveryStatus(stubShipping.getStatus());
        DroneBriefInfo drone = new DroneBriefInfo();
        drone.setId(stubDrone.getId());
        drone.setState(stubDrone.getState());
        shippingInfo.setDrone(drone);
        return shippingInfo;
    }

    @Override
    public List<ShippingLogInfo> trackShipment(int shipmentId) {
        List<ShippingLogInfo> logs = stubShippingLogs.stream().map(entity -> {
            ShippingLogInfo info = new ShippingLogInfo();
            info.setEvent(entity.getEvent());
            info.setTime(entity.getLogTime());
            info.setNewValue(entity.getNewValue());
            return info;
        }).collect(Collectors.toList());
        ShippingLogInfo prev = null;
        for (ShippingLogInfo cur : logs) {
            if (prev != null)
                cur.setOldValue(prev.getNewValue());
            prev = cur;
        }
        return logs;
    }

    private void trackLog(DeliveryStatus delivered) {
        ShippingLog log = new ShippingLog();
        log.setShipping(stubShipping);
        log.setDrone(stubDrone);
        log.setEvent(ShippingEvent.STATUS_CHANGE);
        log.setNewValue(delivered.toString());
        log.setLogTime(LocalDateTime.now());
        stubShippingLogs.add(log);
    }
}
