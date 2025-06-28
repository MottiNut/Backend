/*package com.mottinut.auth.infrastructure.persistence.mappersCode.impl;

import com.mottinut.auth.domain.emalServices.entity.UserVerification;
import com.mottinut.auth.domain.emalServices.entity.UserVerificationJpaEntity;
import com.mottinut.auth.domain.emalServices.enums.VerificationType;
import com.mottinut.auth.domain.emalServices.repositories.UserVerificationJpaRepository;
import com.mottinut.auth.domain.emalServices.repositories.UserVerificationRepository;
import com.mottinut.auth.infrastructure.persistence.mappersCode.UserVerificationMapper;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Repository
@Transactional
@Slf4j
public class UserVerificationRepositoryImpl implements UserVerificationRepository {

    private final UserVerificationJpaRepository jpaRepository;
    private final UserVerificationMapper mapper;

    public UserVerificationRepositoryImpl(UserVerificationJpaRepository jpaRepository,
                                          UserVerificationMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public UserVerification save(UserVerification userVerification) {
        log.debug("Guardando verificación para usuario: {}, tipo: {}",
                userVerification.getUserId().getValue(), userVerification.getType());

        UserVerificationJpaEntity entity = mapper.toJpaEntity(userVerification);
        UserVerificationJpaEntity savedEntity = jpaRepository.save(entity);

        log.debug("Verificación guardada con ID: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UserVerification> findLatestByUserIdAndType(UserId userId, VerificationType type) {
        log.debug("Buscando última verificación para usuario: {}, tipo: {}", userId.getValue(), type);

        Optional<UserVerificationJpaEntity> entity = jpaRepository.findLatestByUserIdAndType(
                userId.getValue(), type);

        if (entity.isPresent()) {
            log.debug("Verificación encontrada: ID {}, creada: {}",
                    entity.get().getId(), entity.get().getCreatedAt());
        } else {
            log.debug("No se encontró verificación para usuario: {}, tipo: {}", userId.getValue(), type);
        }

        return entity.map(mapper::toDomain);
    }

    @Override
    public void deleteByUserId(UserId userId) {
        log.debug("Eliminando todas las verificaciones del usuario: {}", userId.getValue());

        jpaRepository.deleteByUserId(userId.getValue());

        log.debug("Verificaciones eliminadas para usuario: {}", userId.getValue());
    }

    @Override
    public void deleteExpiredVerifications() {
        log.debug("Eliminando verificaciones expiradas");

        LocalDateTime now = LocalDateTime.now();
        jpaRepository.deleteByExpiresAtBefore(now);

        log.debug("Verificaciones expiradas eliminadas hasta: {}", now);
    }

    @Override
    public boolean existsByUserIdAndTypeAndNotExpired(UserId userId, VerificationType type) {
        log.debug("Verificando si existe verificación activa para usuario: {}, tipo: {}",
                userId.getValue(), type);

        Optional<UserVerification> verification = findLatestByUserIdAndType(userId, type);

        if (verification.isEmpty()) {
            log.debug("No existe verificación para usuario: {}, tipo: {}", userId.getValue(), type);
            return false;
        }

        UserVerification v = verification.get();
        boolean isNotExpired = v.getExpiresAt().isAfter(LocalDateTime.now());

        log.debug("Verificación encontrada - Expirada: {}, Verificada: {}",
                !isNotExpired, v.isVerified());

        return isNotExpired;
    }

    @Override
    public Optional<UserVerification> findValidVerification(UserId userId, VerificationType type, String code) {
        log.debug("Buscando verificación válida para usuario: {}, tipo: {}, código: {}",
                userId.getValue(), type, code);

        Optional<UserVerification> verification = findLatestByUserIdAndType(userId, type);

        if (verification.isEmpty()) {
            log.debug("No se encontró verificación para usuario: {}, tipo: {}", userId.getValue(), type);
            return Optional.empty();
        }

        UserVerification v = verification.get();

        // Verificar que no esté expirada
        if (v.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.debug("Verificación expirada para usuario: {}, tipo: {}", userId.getValue(), type);
            return Optional.empty();
        }

        // Verificar que el código coincida
        if (!v.getCode().equals(code)) {
            log.debug("Código incorrecto para usuario: {}, tipo: {}", userId.getValue(), type);
            return Optional.empty();
        }

        // Verificar que no haya sido ya verificada
        if (v.isVerified()) {
            log.debug("Verificación ya utilizada para usuario: {}, tipo: {}", userId.getValue(), type);
            return Optional.empty();
        }

        log.debug("Verificación válida encontrada para usuario: {}, tipo: {}", userId.getValue(), type);
        return verification;
    }

    @Override
    public UserVerification markAsVerified(UserVerification userVerification) {
        log.debug("Marcando verificación como verificada: ID {}", userVerification.getId());

        UserVerification verifiedVerification = UserVerification.builder()
                .id(userVerification.getId())
                .userId(userVerification.getUserId())
                .code(userVerification.getCode())
                .type(userVerification.getType())
                .contact(userVerification.getContact())
                .createdAt(userVerification.getCreatedAt())
                .expiresAt(userVerification.getExpiresAt())
                .isVerified(true)
                .verifiedAt(LocalDateTime.now())
                .build();

        return save(verifiedVerification);
    }

    @Override
    public long countPendingVerificationsByUserIdAndType(UserId userId, VerificationType type) {
        log.debug("Contando verificaciones pendientes para usuario: {}, tipo: {}",
                userId.getValue(), type);

        Optional<UserVerification> verification = findLatestByUserIdAndType(userId, type);

        if (verification.isEmpty()) {
            return 0;
        }

        UserVerification v = verification.get();

        // Solo contar si no está expirada y no está verificada
        if (v.getExpiresAt().isAfter(LocalDateTime.now()) && !v.isVerified()) {
            log.debug("Verificación pendiente encontrada para usuario: {}, tipo: {}",
                    userId.getValue(), type);
            return 1;
        }

        return 0;
    }
}

*/