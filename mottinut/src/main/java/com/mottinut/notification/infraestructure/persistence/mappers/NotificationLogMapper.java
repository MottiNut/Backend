package com.mottinut.notification.infraestructure.persistence.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mottinut.notification.domain.entities.NotificationLog;
import com.mottinut.notification.domain.valueobjects.NotificationContent;
import com.mottinut.notification.infraestructure.persistence.entities.NotificationLogEntity;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationLogMapper {

    private final ObjectMapper objectMapper;

    public NotificationLogEntity toEntity(NotificationLog domainObject) {
        try {
            String dataJson = domainObject.getContent().getData() != null
                    ? objectMapper.writeValueAsString(domainObject.getContent().getData())
                    : null;

            return NotificationLogEntity.builder()
                    .id(domainObject.getId())
                    .userId(domainObject.getUserId().getValue())
                    .title(domainObject.getContent().getTitle())
                    .body(domainObject.getContent().getBody())
                    .data(dataJson)
                    .status(domainObject.getStatus())
                    .externalId(domainObject.getExternalId())
                    .createdAt(domainObject.getCreatedAt() != null
                            ? domainObject.getCreatedAt()
                            : LocalDateTime.now()) // ← este fix
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error mapping NotificationLog to Entity", e);
        }
    }


    public NotificationLog toDomainObject(NotificationLogEntity entity) {
        try {
            Map<String, String> data = entity.getData() != null
                    ? objectMapper.readValue(entity.getData(), new TypeReference<>() {
            })
                    : null;

            NotificationContent content = NotificationContent.builder()
                    .title(entity.getTitle())
                    .body(entity.getBody())
                    .data(data)
                    .build();

            return NotificationLog.builder()
                    .id(entity.getId())
                    .userId(UserId.of(entity.getUserId()))
                    .content(content)
                    .status(entity.getStatus())
                    .externalId(entity.getExternalId())
                    .createdAt(entity.getCreatedAt())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error mapping NotificationLogEntity to Domain", e);
        }
    }
}
