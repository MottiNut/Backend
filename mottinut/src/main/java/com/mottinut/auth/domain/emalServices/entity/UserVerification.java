package com.mottinut.auth.domain.emalServices.entity;

import com.mottinut.auth.domain.emalServices.enums.VerificationType;
import com.mottinut.auth.domain.emalServices.valueObject.VerificationCode;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_verifications")
@Getter
@NoArgsConstructor
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "user_id"))
    private UserId userId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "verification_code"))
    private VerificationCode code;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_type")
    private VerificationType type;

    @Column(name = "contact")
    private String contact; // email o teléfono

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    // Constructor personalizado (mantén el que ya tienes)
    public UserVerification(UserId userId, VerificationCode code, VerificationType type,
                            String contact, int expirationMinutes) {
        this.userId = userId;
        this.code = code;
        this.type = type;
        this.contact = contact;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(expirationMinutes);
        this.isVerified = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean verify(VerificationCode providedCode) {
        if (isExpired()) {
            return false;
        }

        if (this.code.equals(providedCode)) {
            this.isVerified = true;
            this.verifiedAt = LocalDateTime.now();
            return true;
        }

        return false;
    }

    public void markAsVerified() {
        this.isVerified = true;
        this.verifiedAt = LocalDateTime.now();
    }

    public boolean canResend() {
        // Permitir reenvío después de 1 minuto
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(1));
    }
}