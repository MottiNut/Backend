package com.mottinut.notification.domain.valueobjects;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NotificationResult {
    boolean success;
    String externalId;
    String error;
    boolean tokenInvalid;

    public static NotificationResult success(String externalId) {
        return NotificationResult.builder()
                .success(true)
                .externalId(externalId)
                .build();
    }

    public static NotificationResult failure(String error, boolean tokenInvalid) {
        return NotificationResult.builder()
                .success(false)
                .error(error)
                .tokenInvalid(tokenInvalid)
                .build();
    }
}
