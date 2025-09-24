package com.mottinut.auth.domain.services;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.entities.UserSettings;
import com.mottinut.auth.domain.repositories.UserSettingsRepository;
import com.mottinut.auth.domain.repositories.UserRepository;
import com.mottinut.auth.infrastructure.persistence.entities.UserEntity;
import com.mottinut.auth.infrastructure.persistence.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserSettingsService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserSettings getOrCreateSettings(User user) {

        UserEntity userEntity = userRepository.findById(user.getUserId())
                .map(userMapper::toEntity)
                .orElseThrow(() -> new RuntimeException("UserEntity no encontrado"));

        return userSettingsRepository.findByUser(userEntity)
                .orElseGet(() -> {
                    UserSettings settings = UserSettings.builder()
                            .user(userEntity)
                            .onboardingSeen(false)
                            .toolTipsSeen(false)
                            .build();
                    return userSettingsRepository.save(settings);
                });
    }

    public UserSettings setOnboardingSeen(User user, boolean seen) {
        UserSettings settings = getOrCreateSettings(user);
        settings.setOnboardingSeen(seen);
        return userSettingsRepository.save(settings);
    }

    public UserSettings setToolTipsSeen(User user, boolean seen) {
        UserSettings settings = getOrCreateSettings(user);
        settings.setToolTipsSeen(seen);
        return userSettingsRepository.save(settings);
    }
}



