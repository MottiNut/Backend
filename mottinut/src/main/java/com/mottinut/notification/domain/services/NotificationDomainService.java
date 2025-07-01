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
            Optional<UserDeviceToken> existing = deviceTokenRepository.findFullByValue(deviceToken.getValue());

            if (existing.isPresent()) {
                UserDeviceToken existingToken = existing.get();

                if (existingToken.getUserId().equals(userId)) {
                    // El token ya pertenece al usuario actual, solo reactivar
                    deviceTokenRepository.reactivate(userId, deviceToken);
                    log.info("Device token reactivado para user: {}", userId.getValue());
                } else {
                    // El token pertenece a otro usuario, reasignar al usuario actual
                    log.info("Reasignando device token del usuario {} al usuario {}",
                            existingToken.getUserId().getValue(), userId.getValue());

                    // Desactivar el token del usuario anterior
                    deviceTokenRepository.markAsInvalid(existingToken.getUserId(), deviceToken);

                    // Desactivar todos los tokens del usuario actual (por si tenía otros)
                    deviceTokenRepository.deactivateAllByUser(userId);

                    // Registrar el token para el nuevo usuario
                    deviceTokenRepository.save(userId, deviceToken);

                    log.info("Device token reasignado exitosamente al usuario: {}", userId.getValue());
                }
            } else {
                // Token completamente nuevo
                deviceTokenRepository.deactivateAllByUser(userId);
                deviceTokenRepository.save(userId, deviceToken);
                log.info("Device token registrado para user: {}", userId.getValue());
            }
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