package com.mottinut.notification.infraestructure.persistence.repositories;
import com.mottinut.notification.domain.entities.NotificationLog;
import com.mottinut.notification.domain.repository.NotificationRepository;
import com.mottinut.notification.domain.valueobjects.NotificationContent;
import com.mottinut.notification.domain.valueobjects.NotificationStatus;
import com.mottinut.notification.infraestructure.persistence.jpa.NotificationLogJpaRepository;
import com.mottinut.notification.infraestructure.persistence.mappers.NotificationLogMapper;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaNotificationRepository implements NotificationRepository {

    private final NotificationLogJpaRepository jpaRepository;
    private final NotificationLogMapper mapper;

    @Override
    public void saveNotificationLog(UserId userId, NotificationContent content,
                                    NotificationStatus status, String externalId) {
        NotificationLog domainLog = NotificationLog.builder()
                .userId(userId)
                .content(content)
                .status(status)
                .externalId(externalId)
                .build();

        jpaRepository.save(mapper.toEntity(domainLog));
    }

    @Override
    public List<NotificationLog> findRecentByUserId(UserId userId, int limit) {
        return jpaRepository.findByUserIdOrderByCreatedAtDesc(userId.getValue(), PageRequest.of(0, limit))
                .stream()
                .map(mapper::toDomainObject)
                .collect(Collectors.toList());
    }
}