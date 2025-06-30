package com.mottinut.notification.domain.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mottinut.notification.domain.entities.UserDeviceToken;
import com.mottinut.notification.domain.repository.UserDeviceTokenRepository;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserDeviceTokenRepository deviceTokenRepository;

    public NotificationService(FirebaseMessaging firebaseMessaging, UserDeviceTokenRepository deviceTokenRepository) {
        this.firebaseMessaging = firebaseMessaging;
        this.deviceTokenRepository = deviceTokenRepository;
    }

    public void sendPlanApprovedNotification(UserId patientId, String patientName, String nutritionistName) {
        deviceTokenRepository.findByUserId(patientId.getValue())
                .ifPresent(tokenEntity -> {
                    Message message = Message.builder()
                            .setToken(tokenEntity.getDeviceToken())
                            .setNotification(Notification.builder()
                                    .setTitle("Plan Nutricional Aprobado")
                                    .setBody("Tu plan nutricional ha sido aprobado por " + nutritionistName)
                                    .build())
                            .putData("type", "PLAN_APPROVED")
                            .putData("patientName", patientName)
                            .build();

                    try {
                        firebaseMessaging.send(message);
                    } catch (FirebaseMessagingException e) {
                        System.err.println("Error enviando notificación: " + e.getMessage());
                    }
                });
    }

    public void saveDeviceToken(UserId userId, String deviceToken) {
        UserDeviceToken token = deviceTokenRepository.findByUserId(userId.getValue())
                .orElse(new UserDeviceToken(userId.getValue(), deviceToken));
        token.setDeviceToken(deviceToken);
        deviceTokenRepository.save(token);
    }
}