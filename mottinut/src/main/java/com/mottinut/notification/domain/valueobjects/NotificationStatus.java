package com.mottinut.notification.domain.valueobjects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationStatus {
    PENDING("pending"),
    SENT("sent"),
    FAILED("failed"),
    ERROR("error");

    private final String value;
}
