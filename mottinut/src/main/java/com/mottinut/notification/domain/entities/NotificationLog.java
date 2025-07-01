package com.mottinut.notification.domain.entities;

import com.mottinut.notification.domain.valueobjects.NotificationContent;
import com.mottinut.notification.domain.valueobjects.NotificationStatus;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationLog {
    private final Long id;
    private final UserId userId;
    private final NotificationContent content;
    private final NotificationStatus status;
    private final String externalId;
    private final LocalDateTime createdAt;

    // Lógica de negocio
    public boolean isSuccessful() {
        return status == NotificationStatus.SENT;
    }

    public boolean hasFailed() {
        return status == NotificationStatus.FAILED || status == NotificationStatus.ERROR;
    }

    public boolean isRecent() {
        return createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    public boolean belongsToUser(UserId userId) {
        return this.userId.equals(userId);
    }

    public String getStatusDescription() {
        return switch (status) {
            case SENT -> "Notification delivered successfully";
            case FAILED -> "Failed to deliver notification";
            case ERROR -> "Error occurred during delivery";
            case PENDING -> "Notification pending delivery";
        };
    }
}