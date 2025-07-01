package com.mottinut.notification.infraestructure.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mottinut.notification.domain.services.NotificationSender;
import com.mottinut.notification.domain.valueobjects.DeviceToken;
import com.mottinut.notification.domain.valueobjects.NotificationContent;
import com.mottinut.notification.domain.valueobjects.NotificationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseNotificationSender implements NotificationSender {
    private final FirebaseMessaging firebaseMessaging;

    @Override
    public NotificationResult send(DeviceToken deviceToken, NotificationContent content) {
        try {
            log.debug("Sending notification to token: {}", deviceToken.getValue());

            Notification notification = Notification.builder()
                    .setTitle(content.getTitle())
                    .setBody(content.getBody())
                    .build();

            Message message = Message.builder()
                    .setToken(deviceToken.getValue())
                    .setNotification(notification)
                    .putAllData(content.getData())
                    .build();

            String response = firebaseMessaging.send(message);
            return NotificationResult.success(response);

        } catch (FirebaseMessagingException e) {
            log.error("Firebase error sending notification: {}", e.getMessage(), e);
            boolean isTokenInvalid = isTokenError(e);
            return NotificationResult.failure(e.getMessage(), isTokenInvalid);
        } catch (Exception e) {
            log.error("Unexpected error sending notification: {}", e.getMessage(), e);
            return NotificationResult.failure(e.getMessage(), false);
        }
    }

    private boolean isTokenError(FirebaseMessagingException e) {
        String errorCode = String.valueOf(e.getErrorCode());
        return "UNREGISTERED".equals(errorCode) ||
                "INVALID_ARGUMENT".equals(errorCode) ||
                "NOT_FOUND".equals(errorCode);
    }
}

