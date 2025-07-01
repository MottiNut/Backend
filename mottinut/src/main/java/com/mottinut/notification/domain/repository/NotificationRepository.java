package com.mottinut.notification.domain.repository;

import com.mottinut.notification.domain.entities.NotificationLog;
import com.mottinut.notification.domain.valueobjects.NotificationContent;
import com.mottinut.notification.domain.valueobjects.NotificationStatus;
import com.mottinut.shared.domain.valueobjects.UserId;

import java.util.List;

public interface NotificationRepository {
    void saveNotificationLog(UserId userId, NotificationContent content, NotificationStatus status, String externalId);
    List<NotificationLog> findRecentByUserId(UserId userId, int limit);
}
