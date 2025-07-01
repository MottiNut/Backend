package com.mottinut.notification.domain.exceptions;

public class NotificationSendException extends RuntimeException {
    public NotificationSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
