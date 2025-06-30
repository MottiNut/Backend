package com.mottinut.notification.domain.services;

import com.mottinut.notification.domain.entities.UserDeviceToken;
import com.mottinut.notification.domain.repository.UserDeviceTokenRepository;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class DeviceTokenService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceTokenService.class);

    private final UserDeviceTokenRepository userDeviceTokenRepository;

    public DeviceTokenService(UserDeviceTokenRepository userDeviceTokenRepository) {
        this.userDeviceTokenRepository = userDeviceTokenRepository;
    }

    public void saveDeviceToken(UserId userId, String deviceToken) {
        if (deviceToken != null && !deviceToken.isEmpty()) {
            try {
                // Buscar si ya existe un token para este usuario
                Optional<UserDeviceToken> existingToken = userDeviceTokenRepository.findByUserId(userId.getValue());

                if (existingToken.isPresent()) {
                    // Actualizar token existente
                    UserDeviceToken userToken = existingToken.get();
                    userToken.setDeviceToken(deviceToken);
                    userToken.setCreatedAt(java.time.LocalDateTime.now()); // Actualizar timestamp
                    userDeviceTokenRepository.save(userToken);
                    logger.info("Device token updated for user: {}", userId.getValue());
                } else {
                    // Crear nuevo token
                    UserDeviceToken newToken = new UserDeviceToken(userId.getValue(), deviceToken);
                    userDeviceTokenRepository.save(newToken);
                    logger.info("New device token saved for user: {}", userId.getValue());
                }
            } catch (Exception e) {
                logger.error("Error saving device token for user {}: {}", userId.getValue(), e.getMessage(), e);
                throw new RuntimeException("Error saving device token", e);
            }
        } else {
            logger.warn("Attempted to save null or empty device token for user: {}", userId.getValue());
        }
    }

    public String getDeviceToken(UserId userId) {
        try {
            Optional<UserDeviceToken> userToken = userDeviceTokenRepository.findByUserId(userId.getValue());
            if (userToken.isPresent()) {
                String token = userToken.get().getDeviceToken();
                logger.debug("Device token retrieved for user: {}", userId.getValue());
                return token;
            } else {
                logger.debug("No device token found for user: {}", userId.getValue());
                return null;
            }
        } catch (Exception e) {
            logger.error("Error retrieving device token for user {}: {}", userId.getValue(), e.getMessage(), e);
            return null;
        }
    }

    public void removeDeviceToken(UserId userId) {
        try {
            Optional<UserDeviceToken> userToken = userDeviceTokenRepository.findByUserId(userId.getValue());
            if (userToken.isPresent()) {
                userDeviceTokenRepository.delete(userToken.get());
                logger.info("Device token removed for user: {}", userId.getValue());
            } else {
                logger.warn("Attempted to remove non-existent device token for user: {}", userId.getValue());
            }
        } catch (Exception e) {
            logger.error("Error removing device token for user {}: {}", userId.getValue(), e.getMessage(), e);
            throw new RuntimeException("Error removing device token", e);
        }
    }

    public boolean hasDeviceToken(UserId userId) {
        try {
            Optional<UserDeviceToken> userToken = userDeviceTokenRepository.findByUserId(userId.getValue());
            boolean hasToken = userToken.isPresent() &&
                    userToken.get().getDeviceToken() != null &&
                    !userToken.get().getDeviceToken().trim().isEmpty();
            logger.debug("User {} has device token: {}", userId.getValue(), hasToken);
            return hasToken;
        } catch (Exception e) {
            logger.error("Error checking device token for user {}: {}", userId.getValue(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * Limpia tokens inválidos/expirados si Firebase indica que son inválidos
     */
    public void markTokenAsInvalid(UserId userId) {
        try {
            removeDeviceToken(userId);
            logger.info("Invalid device token removed for user: {}", userId.getValue());
        } catch (Exception e) {
            logger.error("Error marking token as invalid for user {}: {}", userId.getValue(), e.getMessage(), e);
        }
    }
}