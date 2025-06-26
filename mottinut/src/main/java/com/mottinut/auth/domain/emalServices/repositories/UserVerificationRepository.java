package com.mottinut.auth.domain.emalServices.repositories;

import com.mottinut.auth.domain.emalServices.entity.UserVerification;
import com.mottinut.auth.domain.emalServices.enums.VerificationType;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {

    @Query("SELECT uv FROM UserVerification uv WHERE uv.userId = :userId AND uv.type = :type ORDER BY uv.createdAt DESC LIMIT 1")
    Optional<UserVerification> findLatestByUserIdAndType(@Param("userId") UserId userId, @Param("type") VerificationType type);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserVerification uv WHERE uv.userId = :userId")
    void deleteByUserId(@Param("userId") UserId userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserVerification uv WHERE uv.expiresAt < :now")
    int deleteExpiredVerifications(@Param("now") LocalDateTime now);

    // Fixed: Use 'isVerified' instead of 'verified' to match the entity field
    @Query("SELECT CASE WHEN COUNT(uv) > 0 THEN true ELSE false END FROM UserVerification uv WHERE uv.userId = :userId AND uv.type = :type AND uv.expiresAt > :now AND uv.isVerified = false")
    boolean existsByUserIdAndTypeAndNotExpired(@Param("userId") UserId userId, @Param("type") VerificationType type, @Param("now") LocalDateTime now);

    // Fixed: Use 'isVerified' instead of 'verified' and proper code comparison
    @Query("SELECT uv FROM UserVerification uv WHERE uv.userId = :userId AND uv.type = :type AND uv.code.value = :code AND uv.expiresAt > :now AND uv.isVerified = false")
    Optional<UserVerification> findValidVerification(@Param("userId") UserId userId, @Param("type") VerificationType type, @Param("code") String code, @Param("now") LocalDateTime now);

    // Fixed: Use 'isVerified' instead of 'verified'
    @Query("SELECT COUNT(uv) FROM UserVerification uv WHERE uv.userId = :userId AND uv.type = :type AND uv.isVerified = false AND uv.expiresAt > :now")
    long countPendingVerificationsByUserIdAndType(@Param("userId") UserId userId, @Param("type") VerificationType type, @Param("now") LocalDateTime now);
}