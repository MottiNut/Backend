package com.mottinut.auth.infrastructure.persistence.entities;

import com.mottinut.auth.domain.valueobjects.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String phone;

    // Imagen de perfil - AQUÍ ESTÁ EL CAMBIO
    @Lob
    @Column(name = "profile_image", columnDefinition = "MEDIUMBLOB")
    private byte[] profileImage;

    @Column(name = "image_content_type")
    private String imageContentType;

    // Campos de verificación
    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "phone_verified_at")
    private LocalDateTime phoneVerifiedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Campo para identificar el tipo de usuario
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private Role userType;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}