package com.mottinut.notification.infraestructure.persistence.jpa;


import com.mottinut.notification.infraestructure.persistence.entities.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationLogJpaRepository extends JpaRepository<NotificationLogEntity, Long> {
    List<NotificationLogEntity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
