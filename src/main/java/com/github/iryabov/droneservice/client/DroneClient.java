package com.github.iryabov.droneservice.client;

import com.github.iryabov.droneservice.entity.DroneModel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public interface DroneClient {

    Driver lookup(String droneSerial, DroneModel model);

    interface Driver {
        void flyTo(Point location);
        void flyToBase();
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

        @Override
        public String toString() {
            return String.format("(%.2f, %.2f)", lat, lon);
        }
    }
}
