package com.mottinut.auth.domain.repositories;


import com.mottinut.auth.domain.entities.UserSettings;
import com.mottinut.auth.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.mottinut.auth.infrastructure.persistence.entities.UserEntity;


public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUser(UserEntity user);
}


