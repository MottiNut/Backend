package com.mottinut.auth.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;

@Data
public class RegisterPatientRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotNull
    private LocalDate birthDate;
    private String phone;
    private Double height;
    private Double weight;
    private boolean hasMedicalCondition = false;
    private String chronicDisease;
    private String allergies;
    private String dietaryPreferences;
}