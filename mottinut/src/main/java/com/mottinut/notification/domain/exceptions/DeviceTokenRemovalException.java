package com.mottinut.notification.domain.exceptions;

public class DeviceTokenRemovalException extends RuntimeException {
    public DeviceTokenRemovalException(String message, Throwable cause) {
        super(message, cause);
    }
}
