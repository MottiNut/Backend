package com.mottinut.auth.domain.repositories;

import com.mottinut.auth.domain.entities.ShareCode;
import com.mottinut.auth.domain.entities.ShareClick;
import com.mottinut.auth.domain.entities.ProfilePrivacySettings;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShareCodeRepository extends JpaRepository<ShareCode, Long> {

    Optional<ShareCode> findByShortCodeAndIsActiveTrue(String shortCode);

    Optional<ShareCode> findByUserIdAndUserTypeAndIsActiveTrue(Long userId, ShareCode.UserType userType);

    @Query("SELECT sc FROM ShareCode sc WHERE sc.userId = :userId AND sc.userType = :userType AND sc.isActive = true")
    Optional<ShareCode> findActiveByUserIdAndType(@Param("userId") Long userId, @Param("userType") ShareCode.UserType userType);

    @Query("SELECT sc FROM ShareCode sc WHERE sc.expiresAt < :now AND sc.isActive = true")
    List<ShareCode> findExpiredCodes(@Param("now") LocalDateTime now);

    boolean existsByShortCode(String shortCode);

    @Query("SELECT COUNT(sc) FROM ShareCode sc WHERE sc.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
}
