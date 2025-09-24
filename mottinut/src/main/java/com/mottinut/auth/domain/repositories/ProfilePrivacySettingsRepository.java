package com.mottinut.auth.domain.repositories;

import com.mottinut.auth.domain.entities.ProfilePrivacySettings;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilePrivacySettingsRepository extends JpaRepository<ProfilePrivacySettings, Long> {

    Optional<ProfilePrivacySettings> findByUserId(Long userId);

    @Query("SELECT pps FROM ProfilePrivacySettings pps WHERE pps.userId = :userId")
    Optional<ProfilePrivacySettings> findByUserIdValue(@Param("userId") Long userId);
}