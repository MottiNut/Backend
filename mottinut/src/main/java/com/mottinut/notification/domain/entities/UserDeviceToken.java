package com.mottinut.notification.domain.entities;

import com.mottinut.notification.domain.valueobjects.DeviceToken;
import com.mottinut.notification.domain.valueobjects.Platform;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserDeviceToken {
    private final Long id;
    private final UserId userId;
    private final DeviceToken deviceToken;
    private final Platform platform;
    private final boolean isActive;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime lastUsedAt;

    // Lógica de negocio
    public boolean isValid() {
        return isActive && deviceToken != null && !deviceToken.getValue().trim().isEmpty();
    }

    public boolean hasBeenUsedRecently() {
        if (lastUsedAt == null) return false;
        return lastUsedAt.isAfter(LocalDateTime.now().minusDays(30));
    }

    public boolean isExpired() {
        if (lastUsedAt == null) return false;
        return lastUsedAt.isBefore(LocalDateTime.now().minusDays(90));
    }

    public boolean belongsToUser(UserId userId) {
        return this.userId.equals(userId);
    }

    public boolean supportsRichNotifications() {
        return platform == Platform.IOS || platform == Platform.ANDROID;
    }

    public UserDeviceToken markAsUsed() {
        return UserDeviceToken.builder()
                .id(this.id)
                .userId(this.userId)
                .deviceToken(this.deviceToken)
                .platform(this.platform)
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .lastUsedAt(LocalDateTime.now())
                .build();
    }

    public UserDeviceToken deactivate() {
        return UserDeviceToken.builder()
                .id(this.id)
                .userId(this.userId)
                .deviceToken(this.deviceToken)
                .platform(this.platform)
                .isActive(false)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .lastUsedAt(this.lastUsedAt)
                .build();
    }

    public UserDeviceToken updateToken(DeviceToken newToken) {
        return UserDeviceToken.builder()
                .id(this.id)
                .userId(this.userId)
                .deviceToken(newToken)
                .platform(this.platform)
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .lastUsedAt(this.lastUsedAt)
                .build();
    }
}