package com.mottinut.auth.infrastructure.persistence.mappersCode.impl;

import com.mottinut.auth.domain.emalServices.repositories.UserVerificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Component
@Slf4j
public class VerificationCleanupScheduler {

    private final UserVerificationRepository userVerificationRepository;

    public VerificationCleanupScheduler(UserVerificationRepository userVerificationRepository) {
        this.userVerificationRepository = userVerificationRepository;
    }

    @Scheduled(fixedRate = 3600000) // 1 hora en milisegundos
    public void cleanupExpiredVerifications() {
        log.info("Iniciando limpieza de verificaciones expiradas");

        try {
            // FIX: Pass LocalDateTime.now() as parameter
            LocalDateTime now = LocalDateTime.now();
            int deletedCount = userVerificationRepository.deleteExpiredVerifications(LocalDateTime.now());
            log.info("Limpieza de verificaciones expiradas completada. Registros eliminados: {}", deletedCount);
        } catch (Exception e) {
            log.error("Error durante la limpieza de verificaciones expiradas", e);
        }
    }
}
// 6. Script SQL para crear la tabla (para referencia)
/*
CREATE TABLE user_verifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    verification_code VARCHAR(6) NOT NULL,
    verification_type VARCHAR(20) NOT NULL,
    contact VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP NULL,

    INDEX idx_user_id_type (user_id, verification_type),
    INDEX idx_expires_at (expires_at),

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
*/