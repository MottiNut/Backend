package com.mottinut.auth.domain.emalServices.repositories;

import com.mottinut.auth.domain.emalServices.entity.UserVerificationJpaEntity;
import com.mottinut.auth.domain.emalServices.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserVerificationJpaRepository extends JpaRepository<UserVerificationJpaEntity, Long> {

    @Query("SELECT v FROM UserVerificationJpaEntity v WHERE v.userId = :userId AND v.type = :type ORDER BY v.createdAt DESC")
    Optional<UserVerificationJpaEntity> findLatestByUserIdAndType(@Param("userId") Long userId, @Param("type") VerificationType type);

    @Modifying
    @Query("DELETE FROM UserVerificationJpaEntity v WHERE v.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM UserVerificationJpaEntity v WHERE v.expiresAt < :now")
    void deleteByExpiresAtBefore(@Param("now") LocalDateTime now);
}
