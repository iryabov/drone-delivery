package com.github.iryabov.droneservice.service;

import com.github.iryabov.droneservice.model.*;

import java.util.List;

public interface ShippingService {
    List<DroneBriefInfo> getDronesReadyForLoading();
    List<DroneBriefInfo> getDronesReadyForShipping();
    int load(int droneId, PackageForm shippingPackage);
    void send(int droneId, DeliveryAddressForm destination);
    void returnBack(int droneId);
    void unload(int droneId);
    ShippingInfo getShippingInfo(int shipmentId);
    List<ShippingLogInfo> trackShipment(int shipmentId);
}
