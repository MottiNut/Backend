package com.mottinut.patient.presentation.dto;

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
    private LocalDate birthDate;
    private Integer age;
    private String phone;
    private Double height;
    private Double weight;
    private Double bmi;
    private Boolean hasMedicalCondition;
    private String chronicDisease;
    private String allergies;
    private String dietaryPreferences;
    private String emergencyContact;
    private LocalDateTime createdAt;
}
