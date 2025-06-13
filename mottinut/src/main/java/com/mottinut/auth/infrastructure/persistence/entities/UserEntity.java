package com.mottinut.auth.infrastructure.persistence.entities;

import com.mottinut.auth.domain.valueobjects.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.usertype.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)  // Cambio clave aquí
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

