package com.mottinut.notification.infraestructure.persistence.repositories;

import com.mottinut.notification.domain.entities.UserDeviceToken;
import com.mottinut.notification.domain.repository.DeviceTokenRepository;
import com.mottinut.notification.domain.valueobjects.DeviceToken;
import com.mottinut.notification.infraestructure.persistence.entities.UserDeviceTokenEntity;
import com.mottinut.notification.infraestructure.persistence.jpa.UserDeviceTokenJpaRepository;
import com.mottinut.notification.infraestructure.persistence.mappers.UserDeviceTokenMapper;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JpaDeviceTokenRepository implements DeviceTokenRepository {

    private final UserDeviceTokenJpaRepository jpaRepository;
    private final UserDeviceTokenMapper mapper;

    @Override
    public Optional<DeviceToken> findByUserId(UserId userId) {
        return jpaRepository.findByUserIdAndIsActiveTrue(userId.getValue())
                .map(mapper::toDomainObject)
                .map(UserDeviceToken::getDeviceToken);
    }

    @Override
    public List<DeviceToken> findAllActiveByUserId(UserId userId) {
        return jpaRepository.findAllByUserIdAndIsActiveTrue(userId.getValue())
                .stream()
                .map(mapper::toDomainObject)
                .map(UserDeviceToken::getDeviceToken)
                .collect(Collectors.toList());
    }

    @Override
    public void save(UserId userId, DeviceToken deviceToken) {
        log.info("=== SAVE DEVICE TOKEN ===");
        log.info("User ID: {}", userId.getValue());
        log.info("Device Token: {}...", deviceToken.getValue().substring(0, 10));
        log.info("Platform: {}", deviceToken.getPlatform());

        try {
            // Buscar si ya existe un token con ese valor (independientemente del usuario)
            Optional<UserDeviceTokenEntity> existingByToken = jpaRepository.findByDeviceToken(deviceToken.getValue());

            log.info("Token existente por valor: {}", existingByToken.isPresent());

            if (existingByToken.isPresent()) {
                log.info("Actualizando token existente");
                // Si existe, actualizar ese registro con el nuevo usuario
                UserDeviceTokenEntity entity = existingByToken.get();
                entity.setUserId(userId.getValue());
                entity.setIsActive(true);
                entity.setUpdatedAt(LocalDateTime.now());
                entity.setLastUsedAt(LocalDateTime.now());
                entity.setPlatform(deviceToken.getPlatform());

                UserDeviceTokenEntity saved = jpaRepository.save(entity);
                log.info("✅ Token actualizado con ID: {}", saved.getId());

            } else {
                // Si no existe, buscar si el usuario ya tiene un token activo
                Optional<UserDeviceTokenEntity> existingByUser = jpaRepository.findByUserIdAndIsActiveTrue(userId.getValue());

                log.info("Token existente por usuario: {}", existingByUser.isPresent());

                if (existingByUser.isPresent()) {
                    log.info("Actualizando token del usuario");
                    // Actualizar el token existente del usuario
                    UserDeviceTokenEntity entity = existingByUser.get();
                    entity.setDeviceToken(deviceToken.getValue());
                    entity.setPlatform(deviceToken.getPlatform());
                    entity.setUpdatedAt(LocalDateTime.now());
                    entity.setLastUsedAt(LocalDateTime.now());

                    UserDeviceTokenEntity saved = jpaRepository.save(entity);
                    log.info("✅ Token de usuario actualizado con ID: {}", saved.getId());

                } else {
                    log.info("Creando nuevo token");
                    // Crear nuevo token directamente como entity
                    UserDeviceTokenEntity newEntity = new UserDeviceTokenEntity();
                    newEntity.setUserId(userId.getValue());
                    newEntity.setDeviceToken(deviceToken.getValue());
                    newEntity.setPlatform(deviceToken.getPlatform());
                    newEntity.setIsActive(true);
                    newEntity.setCreatedAt(LocalDateTime.now());
                    newEntity.setUpdatedAt(LocalDateTime.now());
                    newEntity.setLastUsedAt(LocalDateTime.now());

                    UserDeviceTokenEntity saved = jpaRepository.save(newEntity);
                    log.info("✅ Nuevo token creado con ID: {}", saved.getId());
                }
            }

            // Verificación final
            boolean exists = jpaRepository.existsByUserIdAndIsActiveTrue(userId.getValue());
            log.info("🔍 Verificación final - Token existe: {}", exists);

            if (!exists) {
                log.error("❌ CRITICAL: Token no existe después de save!");
                throw new RuntimeException("Token not persisted correctly");
            } else {
                log.info("✅ SUCCESS: Token confirmado en BD");
            }

        } catch (Exception e) {
            log.error("❌ Error completo en save: ", e);
            throw e;
        }
    }

    @Override
    public void remove(UserId userId) {
        jpaRepository.findByUserIdAndIsActiveTrue(userId.getValue())
                .map(mapper::toDomainObject)
                .ifPresent(domainToken -> {
                    UserDeviceToken deactivated = domainToken.deactivate();
                    jpaRepository.save(mapper.toEntity(deactivated));
                });
    }

    @Override
    public void markAsInvalid(UserId userId, DeviceToken deviceToken) {
        jpaRepository.findByUserIdAndDeviceTokenAndIsActiveTrue(userId.getValue(), deviceToken.getValue())
                .map(mapper::toDomainObject)
                .ifPresent(domainToken -> {
                    UserDeviceToken deactivated = domainToken.deactivate();
                    jpaRepository.save(mapper.toEntity(deactivated));
                });
    }

    @Override
    public boolean existsForUser(UserId userId) {
        return jpaRepository.existsByUserIdAndIsActiveTrue(userId.getValue());
    }

    @Override
    public Optional<DeviceToken> findByValue(String value) {
        return jpaRepository.findByDeviceToken(value)
                .map(mapper::toDomainObject)
                .map(UserDeviceToken::getDeviceToken);
    }

    @Override
    public void deactivateAllByUser(UserId userId) {
        List<UserDeviceTokenEntity> tokens = jpaRepository.findAllByUserIdAndIsActiveTrue(userId.getValue());
        tokens.forEach(entity -> {
            entity.setIsActive(false);
            entity.setUpdatedAt(LocalDateTime.now());
            jpaRepository.save(entity);
        });
    }

    @Override
    public void reactivate(UserId userId, DeviceToken token) {
        Optional<UserDeviceTokenEntity> entityOpt = jpaRepository.findByDeviceToken(token.getValue());
        entityOpt.ifPresent(entity -> {
            entity.setUserId(userId.getValue()); // Asignar al nuevo usuario
            entity.setIsActive(true);
            entity.setLastUsedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());
            jpaRepository.save(entity);
        });
    }

    @Override
    public Optional<UserDeviceToken> findFullByValue(String value) {
        return jpaRepository.findByDeviceToken(value)
                .map(mapper::toDomainObject);
    }
}