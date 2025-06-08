package com.mottinut.auth.domain.entities;

import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {
    // Getters
    @Getter
    private final UserId userId;
    @Getter
    private final Email email;
    @Getter
    private final Password password;
    @Getter
    private final Role role;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    @Getter
    private LocalDate birthDate;
    @Getter
    private String phone;
    @Getter
    private Double height;
    @Getter
    private Double weight;
    private boolean hasMedicalCondition;
    @Getter
    private String chronicDisease;
    @Getter
    private String allergies;
    @Getter
    private String dietaryPreferences;
    @Getter
    private LocalDateTime createdAt;

    public User(UserId userId, Email email, Password password, Role role,
                String firstName, String lastName, LocalDate birthDate, String phone,
                Double height, Double weight, boolean hasMedicalCondition,
                String chronicDisease, String allergies, String dietaryPreferences) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.height = height;
        this.weight = weight;
        this.hasMedicalCondition = hasMedicalCondition;
        this.chronicDisease = chronicDisease;
        this.allergies = allergies;
        this.dietaryPreferences = dietaryPreferences;
        this.createdAt = LocalDateTime.now();
    }

    public boolean hasMedicalCondition() { return hasMedicalCondition; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void updateProfile(String firstName, String lastName, String phone,
                              Double height, Double weight, boolean hasMedicalCondition,
                              String chronicDisease, String allergies, String dietaryPreferences) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.height = height;
        this.weight = weight;
        this.hasMedicalCondition = hasMedicalCondition;
        this.chronicDisease = chronicDisease;
        this.allergies = allergies;
        this.dietaryPreferences = dietaryPreferences;
    }
}