package com.github.iryabov.droneservice.client;

import com.github.iryabov.droneservice.entity.DroneModel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Drone control client
 */
public interface DroneClient {

    /**
     * Find drone by drone serial number and drone model
     * @param droneSerial Drone serial number
     * @param model Drone model
     * @return Driver to control found drone
     */
    Driver lookup(String droneSerial, DroneModel model);

    /**
     * Drone's driver
     */
    interface Driver {
        /**
         * Fly particular coordinates
         * @param location Where should the drone fly
         */
        void flyTo(Point location);

        /**
         * Fly to the base
         */
        void flyToBase();

        /**
         * Unload a package
         */
        void unload();

        /**
         * Load a package
         * @param weight Weight of the package
         */
        void load(double weight);

        /**
         * Get battery charge level
         * @return Battery charge level (%)
         */
        int getBatteryLevel();

        /**
         * Get a current drone location
         * @return Coordinates
         */
        Point getLocation();

        /**
         * Get percent of loading of package
         * @return Loading percent (%)
         */
        int getLoadingPercentage();

        /**
         * The drone has a load?
         * @return Has or not
         */
        boolean hasLoad();

        /**
         * Has the drone reached the destination?
         * @return Reached or not
         */
        boolean isReachedDestination();

        /**
         * Is the drone on the base?
         * @return On the base or not
         */
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
