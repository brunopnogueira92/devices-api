package com.devices.exception;

/**
 * Exception thrown when a device cannot be updated due to invalid state.
 */
public class DeviceInvalidStateException extends RuntimeException {

    public DeviceInvalidStateException(String message) {
        super(message);
    }
}
