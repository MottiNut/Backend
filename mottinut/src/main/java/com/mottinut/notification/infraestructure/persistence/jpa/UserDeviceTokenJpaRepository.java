package com.mottinut.notification.infraestructure.persistence.jpa;

import com.mottinut.notification.infraestructure.persistence.entities.UserDeviceTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDeviceTokenJpaRepository extends JpaRepository<UserDeviceTokenEntity, Long> {
    Optional<UserDeviceTokenEntity> findByUserIdAndIsActiveTrue(Long userId);
    List<UserDeviceTokenEntity> findAllByUserIdAndIsActiveTrue(Long userId);
    Optional<UserDeviceTokenEntity> findByUserIdAndDeviceTokenAndIsActiveTrue(Long userId, String deviceToken);
    boolean existsByUserIdAndIsActiveTrue(Long userId);

    @Modifying
    @Query("UPDATE UserDeviceTokenEntity u SET u.isActive = false WHERE u.userId = :userId")
    void deactivateAllByUserId(@Param("userId") Long userId);

    // 👉 NECESARIOS PARA EVITAR DUPLICADOS

    Optional<UserDeviceTokenEntity> findByDeviceToken(String deviceToken);

    @Modifying
    @Query("UPDATE UserDeviceTokenEntity u SET u.isActive = true, u.updatedAt = CURRENT_TIMESTAMP, u.lastUsedAt = CURRENT_TIMESTAMP WHERE u.deviceToken = :deviceToken")
    void reactivateByDeviceToken(@Param("deviceToken") String deviceToken);
}
