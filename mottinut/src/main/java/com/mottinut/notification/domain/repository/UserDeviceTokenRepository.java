package com.mottinut.notification.domain.repository;

import com.mottinut.notification.domain.entities.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    Optional<UserDeviceToken> findByUserId(Long userId);
    List<UserDeviceToken> findAllByUserId(Long userId); // Para múltiples dispositivos
    void deleteByUserId(Long userId);
}