package com.mottinut.auth.domain.entities;

import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.Getter;

import java.time.LocalDate;

public class Patient extends User {
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
    private String emergencyContact;
    @Getter
    private String gender;

    public Patient(UserId userId, Email email, Password password,
                   String firstName, String lastName, LocalDate birthDate, String phone,
                   Double height, Double weight, boolean hasMedicalCondition,
                   String chronicDisease, String allergies, String dietaryPreferences,
                   String emergencyContact, String gender) {
        super(userId, email, password, Role.PATIENT, firstName, lastName, birthDate, phone);
        this.height = height;
        this.weight = weight;
        this.hasMedicalCondition = hasMedicalCondition;
        this.chronicDisease = chronicDisease;
        this.allergies = allergies;
        this.dietaryPreferences = dietaryPreferences;
        this.emergencyContact = emergencyContact;
        this.gender = gender;
    }

    public boolean hasMedicalCondition() {
        return hasMedicalCondition;
    }

    public void updateMedicalProfile(Double height, Double weight, boolean hasMedicalCondition,
                                     String chronicDisease, String allergies, String dietaryPreferences,
                                     String emergencyContact, String gender) {
        this.height = height;
        this.weight = weight;
        this.hasMedicalCondition = hasMedicalCondition;
        this.chronicDisease = chronicDisease;
        this.allergies = allergies;
        this.dietaryPreferences = dietaryPreferences;
        this.emergencyContact = emergencyContact;
        this.gender = gender;
    }

    public double calculateBMI() {
        if (height == null || weight == null || height <= 0) {
            throw new IllegalStateException("Altura y peso son requeridos para calcular el BMI");
        }
        return weight / Math.pow(height / 100, 2);
    }
}
