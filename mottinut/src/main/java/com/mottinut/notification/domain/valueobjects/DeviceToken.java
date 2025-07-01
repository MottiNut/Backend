package com.mottinut.notification.domain.valueobjects;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DeviceToken {
    String value;
    Platform platform;

    public static DeviceToken of(String token, Platform platform) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Device token cannot be null or empty");
        }
        return DeviceToken.builder()
                .value(token.trim())
                .platform(platform)
                .build();
    }
}
