package com.mottinut.notification.infraestructure.persistence.mappers;

import com.mottinut.notification.domain.entities.UserDeviceToken;
import com.mottinut.notification.domain.valueobjects.DeviceToken;
import com.mottinut.notification.infraestructure.persistence.entities.UserDeviceTokenEntity;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserDeviceTokenMapper {

    public UserDeviceTokenEntity toEntity(UserDeviceToken domainObject) {
        return UserDeviceTokenEntity.builder()
                .id(domainObject.getId())
                .userId(domainObject.getUserId().getValue())
                .deviceToken(domainObject.getDeviceToken().getValue())
                .platform(domainObject.getPlatform())
                .isActive(domainObject.isActive())
                .lastUsedAt(domainObject.getLastUsedAt())
                .build();
    }

    public UserDeviceToken toDomainObject(UserDeviceTokenEntity entity) {
        return UserDeviceToken.builder()
                .id(entity.getId())
                .userId(UserId.of(entity.getUserId()))
                .deviceToken(DeviceToken.of(entity.getDeviceToken(), entity.getPlatform()))
                .platform(entity.getPlatform())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastUsedAt(entity.getLastUsedAt())
                .build();
    }
}

