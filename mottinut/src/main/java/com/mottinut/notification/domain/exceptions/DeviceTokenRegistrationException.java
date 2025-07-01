package com.mottinut.notification.domain.exceptions;

public class DeviceTokenRegistrationException extends RuntimeException {
    public DeviceTokenRegistrationException(String message) {
        super(message);
    }

    public DeviceTokenRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
