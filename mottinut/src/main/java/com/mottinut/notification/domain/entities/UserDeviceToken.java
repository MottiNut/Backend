package com.mottinut.notification.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_device_tokens")
public class UserDeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_token", nullable = false, unique = true)
    private String deviceToken;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();


    public UserDeviceToken(Long userId, String deviceToken) {
        this.userId = userId;
        this.deviceToken = deviceToken;
    }

    public UserDeviceToken() {

    }
}
