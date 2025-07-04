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

@Repository
@RequiredArgsConstructor
@Transactional
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
        // Buscar si ya existe un token con ese valor (independientemente del usuario)
        Optional<UserDeviceTokenEntity> existingByToken = jpaRepository.findByDeviceToken(deviceToken.getValue());

        if (existingByToken.isPresent()) {
            // Si existe, actualizar ese registro con el nuevo usuario
            UserDeviceTokenEntity entity = existingByToken.get();
            entity.setUserId(userId.getValue());
            entity.setIsActive(true);
            entity.setUpdatedAt(LocalDateTime.now());
            entity.setLastUsedAt(LocalDateTime.now());
            entity.setPlatform(deviceToken.getPlatform());
            jpaRepository.save(entity);
        } else {
            // Si no existe, buscar si el usuario ya tiene un token activo
            Optional<UserDeviceToken> existing = jpaRepository.findByUserIdAndIsActiveTrue(userId.getValue())
                    .map(mapper::toDomainObject);

            if (existing.isPresent()) {
                // Actualizar el token existente del usuario
                UserDeviceToken updated = existing.get().updateToken(deviceToken).markAsUsed();
                jpaRepository.save(mapper.toEntity(updated));
            } else {
                // Crear nuevo token
                UserDeviceToken newToken = UserDeviceToken.builder()
                        .userId(userId)
                        .deviceToken(deviceToken)
                        .platform(deviceToken.getPlatform())
                        .isActive(true)
                        .build();
                jpaRepository.save(mapper.toEntity(newToken));
            }
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