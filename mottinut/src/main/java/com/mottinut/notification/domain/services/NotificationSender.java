package com.mottinut.notification.domain.services;

import com.mottinut.notification.domain.valueobjects.DeviceToken;
import com.mottinut.notification.domain.valueobjects.NotificationContent;
import com.mottinut.notification.domain.valueobjects.NotificationResult;


public interface NotificationSender {
    NotificationResult send(DeviceToken deviceToken, NotificationContent content);
}
