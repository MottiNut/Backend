package com.mottinut.auth.domain.entities;

import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile_privacy_settings")
@Getter
@Setter
@NoArgsConstructor
public class ProfilePrivacySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "is_profile_public")
    private Boolean isProfilePublic = true;

    @Column(name = "show_contact_info")
    private Boolean showContactInfo = false;

    @Column(name = "show_location")
    private Boolean showLocation = false;

    @Column(name = "allow_direct_messages")
    private Boolean allowDirectMessages = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ProfilePrivacySettings(UserId userId) {
        this.userId = userId.getValue();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public UserId getUserId() {
        return new UserId(this.userId);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
