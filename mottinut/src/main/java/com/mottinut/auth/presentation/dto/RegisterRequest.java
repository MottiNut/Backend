package com.mottinut.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class RegisterRequest {
    // Getters and Setters
    @NotBlank(message = "El email es requerido")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El rol es requerido")
    @Pattern(regexp = "patient|nutritionist", message = "El rol debe ser 'patient' o 'nutritionist'")
    private String role;

    @NotBlank(message = "El nombre es requerido")
    private String firstName;

    @NotBlank(message = "El apellido es requerido")
    private String lastName;

    @NotNull(message = "La fecha de nacimiento es requerida")
    private LocalDate birthDate;

    @NotBlank(message = "El teléfono es requerido")
    private String phone;

    private Double height;
    private Double weight;
    private boolean hasMedicalCondition = false;
    private String chronicDisease;
    private String allergies;
    private String dietaryPreferences;

    // Constructors
    public RegisterRequest() {}

}
