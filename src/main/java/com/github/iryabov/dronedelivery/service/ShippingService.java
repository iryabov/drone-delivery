package com.github.iryabov.dronedelivery.service;

import com.github.iryabov.dronedelivery.enums.DeliveryStatus;
import com.github.iryabov.dronedelivery.model.*;
import jakarta.annotation.Nullable;

import java.util.List;

/**
 * Drone shipping service
 */
public interface ShippingService {
    /**
     * Get all drones which are ready for loading
     *
     * @return List of drones
     */
    List<DroneBriefInfo> getDronesReadyForLoading();

    /**
     * Get all drones which are ready for shipping
     *
     * @return List of drones
     */
    List<DroneBriefInfo> getDronesReadyForShipping();

    /**
     * Load the drone with a package
     *
     * @param droneId         Drone identifier
     * @param shippingPackage Form of shipping package
     * @return Identifier of shipment
     */
    int load(int droneId, PackageForm shippingPackage);

    /**
     * Send a drone to the destination
     *
     * @param droneId     Drone identifier
     * @param destination Form of delivery address and coordinates
     */
    void send(int droneId, DeliveryAddressForm destination);

    /**
     * Return a drone back to the warehouse
     *
     * @param droneId Identifier of drone
     */
    void returnBack(int droneId);

    /**
     * Upload a package of the drone
     *
     * @param droneId Identifier of drone
     */
    void unload(int droneId);

    /**
     * Get a list of all deliveries by a specific drone
     *
     * @param droneId Drone identifier
     * @param status  Filter by delivery status
     * @param page    Page number (0 by default)
     * @param size    Page size (10 by default)
     * @return List of deliveries
     */
    List<ShippingBriefInfo> getDroneDeliveries(int droneId,
                                               @Nullable DeliveryStatus status,
                                               @Nullable Integer page,
                                               @Nullable Integer size);

    /**
     * Get detailed information about shipping
     *
     * @param droneId    Drone identifier
     * @param shipmentId Shipment identifier
     * @return Information about the shipping
     */
    ShippingDetailedInfo getShippingDetailedInfo(int droneId, int shipmentId);

    /**
     * Get shipping logs
     *
     * @param droneId    Drone identifier
     * @param shipmentId Shipment identifier
     * @return List of logs
     */
    List<ShippingLogInfo> trackShipment(int droneId, int shipmentId);
}
