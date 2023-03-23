package com.github.iryabov.dronedelivery.service;

import com.github.iryabov.dronedelivery.model.*;

import java.util.List;

/**
 * Drone shipping service
 */
public interface ShippingService {
    /**
     * Get all drones which are ready for loading
     * @return List of drones
     */
    List<DroneBriefInfo> getDronesReadyForLoading();

    /**
     * Get all drones which are ready for shipping
     * @return List of drones
     */
    List<DroneBriefInfo> getDronesReadyForShipping();

    /**
     * Load the drone with a package
     * @param droneId Drone identifier
     * @param shippingPackage Form of shipping package
     * @return Identifier of shipment
     */
    int load(int droneId, PackageForm shippingPackage);

    /**
     * Send a drone to the destination
     * @param droneId Drone identifier
     * @param destination Form of delivery address and coordinates
     */
    void send(int droneId, DeliveryAddressForm destination);

    /**
     * Return a drone back to the warehouse
     * @param droneId Identifier of drone
     */
    void returnBack(int droneId);

    /**
     * Upload a package of the drone
     * @param droneId Identifier of drone
     */
    void unload(int droneId);

    /**
     * Get information about shipping
     * @param shipmentId Shipment identifier
     * @return Information about the shipping
     */
    ShippingInfo getShippingInfo(int shipmentId);

    /**
     * Get shipping logs
     * @param shipmentId Shipment identifier
     * @return List of logs
     */
    List<ShippingLogInfo> trackShipment(int shipmentId);
}
