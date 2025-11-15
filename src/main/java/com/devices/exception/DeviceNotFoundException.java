package com.devices.exception;

/**
 * Exception thrown when a device is not found.
 */
public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(Long id) {
        super("Device not found with id: " + id);
    }

    public DeviceNotFoundException(String message) {
        super(message);
    }
}
