package com.mottinut.notification.domain.services;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final FirebaseMessaging firebaseMessaging;
    private final DeviceTokenService deviceTokenService;

    public NotificationService(FirebaseMessaging firebaseMessaging, DeviceTokenService deviceTokenService) {
        this.firebaseMessaging = firebaseMessaging;
        this.deviceTokenService = deviceTokenService;
    }

    public void sendPlanApprovedNotification(UserId patientId, String patientName, Long planId) {
        String deviceToken = deviceTokenService.getDeviceToken(patientId);

        if (deviceToken == null || deviceToken.isEmpty()) {
            logger.warn("No device token found for patient: {}", patientId.getValue());
            return;
        }

        try {
            // ✅ Log para debugging
            System.out.println("🔥 Intentando enviar notificación...");
            System.out.println("📱 Device token: " + deviceToken);
            Notification notification = Notification.builder()
                    .setTitle("¡Plan Nutricional Aprobado!")
                    .setBody("Tu plan nutricional ha sido aprobado por el nutricionista")
                    .build();

            Map<String, String> data = new HashMap<>();
            data.put("type", "PLAN_APPROVED");
            data.put("planId", planId.toString());
            data.put("patientId", patientId.getValue().toString());

            Message message = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(notification)
                    .putAllData(data)
                    .build();

            String response = firebaseMessaging.send(message);
            logger.info("Notification sent successfully to patient {}: {}", patientId.getValue(), response);

        } catch (Exception e) {
            logger.error("Error sending notification to patient {}: {}", patientId.getValue(), e.getMessage(), e);
            System.err.println("❌ Error enviando notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPlanRejectedNotification(UserId patientId, String patientName, Long planId, String reason) {
        String deviceToken = deviceTokenService.getDeviceToken(patientId);

        if (deviceToken == null || deviceToken.isEmpty()) {
            logger.warn("No device token found for patient: {}", patientId.getValue());
            return;
        }

        try {
            Notification notification = Notification.builder()
                    .setTitle("Plan Nutricional Requiere Modificaciones")
                    .setBody("Tu nutricionista ha solicitado algunos ajustes en tu plan")
                    .build();

            Map<String, String> data = new HashMap<>();
            data.put("type", "PLAN_REJECTED");
            data.put("planId", planId.toString());
            data.put("patientId", patientId.getValue().toString());
            data.put("reason", reason != null ? reason : "");

            Message message = Message.builder()
                    .setToken(deviceToken)
                    .setNotification(notification)
                    .putAllData(data)
                    .build();

            String response = firebaseMessaging.send(message);
            logger.info("Rejection notification sent successfully to patient {}: {}", patientId.getValue(), response);

        } catch (Exception e) {
            logger.error("Error sending rejection notification to patient {}: {}", patientId.getValue(), e.getMessage(), e);
        }
    }
}