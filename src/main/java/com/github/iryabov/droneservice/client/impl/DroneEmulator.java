package com.github.iryabov.droneservice.client.impl;

import com.github.iryabov.droneservice.client.DroneClient;

import static java.lang.Math.abs;

public class DroneEmulator implements DroneClient.Driver {
    private final double batteryDischargeRate = 0.2777;
    private final double batteryChargeRate = 0.2777;
    private final double loadingSpeed = 0.2;
    private final double flySpeed;
    private final double weightCapacity;
    private final double batteryCapacity;
    private final DroneClient.Point base;
    private DroneClient.Point currentLocation;
    private DroneClient.Point destination;
    private double currentBattery;
    private double currentLoading = 0;
    private double loadWeight = 0;

    public DroneEmulator(double flySpeed, double weightCapacity, double batteryCapacity, DroneClient.Point base) {
        this.flySpeed = flySpeed;
        this.weightCapacity = weightCapacity;
        this.batteryCapacity = batteryCapacity;
        this.base = base;

        this.destination = base;
        this.currentLocation = base;
        this.currentBattery = batteryCapacity;
    }

    @Override
    public synchronized void flyTo(DroneClient.Point location) {
        destination = location;
    }

    @Override
    public void returnToBase() {
        flyTo(base);
    }

    @Override
    public synchronized void unload() {
        loadWeight = 0;
    }

    @Override
    public synchronized void load(double weight) {
        loadWeight = weight;
        currentLoading = weight;
    }

    public int getBatteryLevel() {
        return (int) (currentBattery * 100 / batteryCapacity);
    }

    @Override
    public DroneClient.Point getLocation() {
        return currentLocation;
    }

    @Override
    public int getLoadingPercentage() {
        return loadWeight > 0 ? 100 - (int)(currentLoading * 100 / loadWeight) : 0;
    }

    @Override
    public boolean hasLoad() {
        return loadWeight > 0;
    }

    @Override
    public boolean isReachedDestination() {
        return currentLocation.equals(destination);
    }

    @Override
    public boolean isOnBase() {
        return currentLocation.equals(base);
    }

    public void compute(int seconds) {
        chargeBattery(seconds);
        loading(seconds);
        lowBattery(seconds);
        move(seconds);
    }

    private synchronized void move(int seconds) {
        if (getBatteryLevel() > 0 && !isReachedDestination()) {
            var dLat = destination.getLat() - currentLocation.getLat();
            var dLon = destination.getLon() - currentLocation.getLon();
            var vLat = dLat > 0 ? 1 : -1;
            var vLon = dLon > 0 ? 1 : -1;
            var sLat = abs(dLat) > flySpeed * seconds ? vLat * flySpeed * seconds : dLat;
            var sLon = abs(dLon) > flySpeed * seconds ? vLon * flySpeed * seconds : dLon;
            currentLocation = new DroneClient.Point(
                    currentLocation.getLat() + sLat,
                    currentLocation.getLon() + sLon);
        }
    }

    private synchronized void lowBattery(int seconds) {
        if (currentBattery > 0 && !isReachedDestination()) {
            double discharge = currentBattery - batteryDischargeRate * seconds;
            currentBattery = Math.max(discharge, 0);
        }
    }

    private synchronized void chargeBattery(int seconds) {
        if (currentBattery < batteryCapacity && isOnBase()) {
            double charge = currentBattery + batteryChargeRate * seconds;
            currentBattery = Math.min(charge, batteryCapacity);
        }
    }

    private synchronized void loading(int seconds) {
        if (loadWeight > 0 && currentLoading > 0) {
            double balance = currentLoading - loadingSpeed * seconds;
            currentLoading = balance > 0 ? balance: 0;
        }
    }
}