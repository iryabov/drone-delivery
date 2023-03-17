package com.github.iryabov.droneservice.service;

import com.github.iryabov.droneservice.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShippingService {
    List<DroneBriefInfo> getDronesReadyForLoading();
    List<DroneBriefInfo> getDronesReadyForShipping();
    int load(int droneId, PackageForm shippingPackage);
    void send(int droneId, DeliveryAddressForm destination);
    void returnBack(int droneId);
    ShippingInfo getShippingInfo(int shipmentId);
    List<ShippingLogInfo> trackShipment(int shipmentId);
}
