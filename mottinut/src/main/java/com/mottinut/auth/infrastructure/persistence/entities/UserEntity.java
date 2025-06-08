package com.mottinut.auth.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "users")
public class UserEntity {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String phone;
    private Double height;
    private Double weight;

    @Column(name = "has_medical_condition")
    private Boolean hasMedicalCondition = false;

    @Column(name = "chronic_disease")
    private String chronicDisease;

    private String allergies;

    @Column(name = "dietary_preferences")
    private String dietaryPreferences;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UserEntity() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
