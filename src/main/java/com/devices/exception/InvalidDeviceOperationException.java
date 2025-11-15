package com.devices.exception;

/**
 * Exception thrown when an invalid operation is attempted on a device.
 */
public class InvalidDeviceOperationException extends RuntimeException {

    public InvalidDeviceOperationException(String message) {
        super(message);
    }
}
