package com.mottinut.patient.domain.entity;

import com.mottinut.patient.domain.valueobjects.PatientId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Getter
@AllArgsConstructor
public class PatientProfile {
    private final PatientId patientId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final LocalDate birthDate;
    private final String phone;
    private final Double height;
    private final Double weight;
    private final boolean hasMedicalCondition;
    private final String chronicDisease;
    private final String allergies;
    private final String dietaryPreferences;
    private final String emergencyContact;
    private final String gender;
    private final LocalDateTime createdAt;


    public String getFullName() {
        return firstName + " " + lastName;
    }

    public double calculateBMI() {
        if (height == null || weight == null || height <= 0) {
            throw new IllegalStateException("Altura y peso son requeridos para calcular el BMI");
        }
        return weight / Math.pow(height / 100, 2);
    }

    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}
