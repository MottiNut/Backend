package com.mottinut.notification.domain.services;

import com.mottinut.notification.domain.entities.UserDeviceToken;
import com.mottinut.notification.domain.exceptions.DeviceTokenRegistrationException;
import com.mottinut.notification.domain.exceptions.DeviceTokenRemovalException;
import com.mottinut.notification.domain.repository.DeviceTokenRepository;
import com.mottinut.notification.domain.repository.NotificationRepository;
import com.mottinut.notification.domain.valueobjects.DeviceToken;
import com.mottinut.notification.domain.valueobjects.NotificationContent;
import com.mottinut.notification.domain.valueobjects.NotificationResult;
import com.mottinut.notification.domain.valueobjects.NotificationStatus;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDomainService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final NotificationSender notificationSender;
    private final NotificationRepository notificationRepository;

    public void sendNotification(UserId userId, NotificationContent content) {
        Optional<DeviceToken> deviceToken = deviceTokenRepository.findByUserId(userId);

        if (deviceToken.isEmpty()) {
            log.warn("No device token found for user: {}", userId.getValue());
            return;
        }

        try {
            NotificationResult result = notificationSender.send(deviceToken.get(), content);

            if (result.isSuccess()) {
                log.info("Notification sent successfully to user {}: {}", userId.getValue(), result.getExternalId());
                notificationRepository.saveNotificationLog(userId, content, NotificationStatus.SENT, result.getExternalId());
            } else {
                log.error("Failed to send notification to user {}: {}", userId.getValue(), result.getError());
                notificationRepository.saveNotificationLog(userId, content, NotificationStatus.FAILED, null);

                if (result.isTokenInvalid()) {
                    deviceTokenRepository.markAsInvalid(userId, deviceToken.get());
                }
            }
        } catch (Exception e) {
            log.error("Error sending notification to user {}: {}", userId.getValue(), e.getMessage(), e);
            notificationRepository.saveNotificationLog(userId, content, NotificationStatus.ERROR, null);
        }
    }

    public void registerDeviceToken(UserId userId, DeviceToken deviceToken) {
        try {
            Optional<UserDeviceToken> existingToken = deviceTokenRepository.findFullByValue(deviceToken.getValue());

            if (existingToken.isPresent()) {
                UserDeviceToken existing = existingToken.get();
                if (!existing.getUserId().equals(userId)) {
                    log.info("Reasignando device token del usuario {} al usuario {}",
                            existing.getUserId().getValue(), userId.getValue());
                } else {
                    log.info("Reactivando device token para el usuario: {}", userId.getValue());
                }
            } else {
                log.info("Registrando nuevo device token para el usuario: {}", userId.getValue());
            }

            // Desactivar todos los tokens del usuario actual (evita duplicados)
            deviceTokenRepository.deactivateAllByUser(userId);

            // Guardar/actualizar el token (el repositorio maneja la reasignación)
            deviceTokenRepository.save(userId, deviceToken);

            log.info("Device token procesado exitosamente para el usuario: {}", userId.getValue());

        } catch (Exception e) {
            log.error("Error registering device token for user {}: {}", userId.getValue(), e.getMessage(), e);
            throw new DeviceTokenRegistrationException("Failed to register device token", e);
        }
    }

    public void removeDeviceToken(UserId userId) {
        try {
            deviceTokenRepository.remove(userId);
            log.info("Device token removed for user: {}", userId.getValue());
        } catch (Exception e) {
            log.error("Error removing device token for user {}: {}", userId.getValue(), e.getMessage(), e);
            throw new DeviceTokenRemovalException("Failed to remove device token", e);
        }
    }
}