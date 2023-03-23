package com.github.iryabov.dronedelivery.exception;

public class DroneDeliveryException extends RuntimeException {
    public DroneDeliveryException(String message) {
        super(message);
    }

    public DroneDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
