package com.github.iryabov.droneservice.client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public interface DroneClient {

    Driver lookup(String droneSerial);

    interface Driver {
        void flyTo(Point location);
        void returnToBase();
        void unload();
        void load(double weight);
        int getBatteryLevel();
        Point getLocation();
        int getLoadingPercentage();
        boolean hasLoad();
        boolean isReachedDestination();
        boolean isOnBase();
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    class Point {
        private double lat, lon;
    }
}
