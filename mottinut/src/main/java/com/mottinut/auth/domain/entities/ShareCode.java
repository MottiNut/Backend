package com.mottinut.auth.domain.entities;

import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "share_codes")
@Getter
@Setter
@NoArgsConstructor
public class ShareCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", unique = true, nullable = false, length = 120)
    private String shortCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 50)
    private UserType userType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "click_count")
    private Integer clickCount = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public enum UserType {
        PATIENT, NUTRITIONIST
    }

    public ShareCode(String shortCode, UserId userId, UserType userType) {
        this.shortCode = shortCode;
        this.userId = userId.getValue();
        this.userType = userType;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusYears(1); // Expira en 1 año
        this.clickCount = 0;
        this.isActive = true;
    }

    public void incrementClickCount() {
        this.clickCount++;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return isActive && !isExpired();
    }

    public UserId getUserId() {
        return new UserId(this.userId);
    }
}

