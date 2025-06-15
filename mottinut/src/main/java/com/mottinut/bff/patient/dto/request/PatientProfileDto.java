package com.mottinut.bff.patient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileDto {
    private Long patientId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String chronicDisease;
    private String allergies;
    private String dietaryPreferences;
    private String emergencyContact;
    private LocalDate birthDate;
    private Integer age;
    private Double height;
    private Double weight;
    private Double bmi;
    private String bmiCategory;
    private Boolean hasMedicalCondition;
    private String gender;
    private LocalDateTime createdAt;
}

