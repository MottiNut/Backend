package com.mottinut.auth.presentation.dto;

import com.mottinut.auth.domain.entities.Nutritionist;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

public class NutritionistResponse {
    // Campos comunes de User
    private Long userId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate birthDate;
    private String phone;
    private LocalDateTime createdAt;

    // Campos específicos de Nutritionist
    private String licenseNumber;
    private String specialization;
    private String workplace;
    private Integer yearsOfExperience;
    private String biography;
    private String experienceLevel; // Calculado

    // Constructor privado
    private NutritionistResponse() {}

    // Factory method
    public static NutritionistResponse fromNutritionist(Nutritionist nutritionist) {
        NutritionistResponse response = new NutritionistResponse();

        // Campos comunes
        response.userId = nutritionist.getUserId().getValue();
        response.email = nutritionist.getEmail().getValue();
        response.role = nutritionist.getRole().getValue();
        response.firstName = nutritionist.getFirstName();
        response.lastName = nutritionist.getLastName();
        response.fullName = nutritionist.getFullName();
        response.birthDate = nutritionist.getBirthDate();
        response.phone = nutritionist.getPhone();
        response.createdAt = nutritionist.getCreatedAt();

        // Campos específicos de Nutritionist
        response.licenseNumber = nutritionist.getLicenseNumber();
        response.specialization = nutritionist.getSpecialization();
        response.workplace = nutritionist.getWorkplace();
        response.yearsOfExperience = nutritionist.getYearsOfExperience();
        response.biography = nutritionist.getBiography();

        // Calcular nivel de experiencia
        response.experienceLevel = calculateExperienceLevel(nutritionist.getYearsOfExperience());

        return response;
    }

    private static String calculateExperienceLevel(Integer years) {
        if (years == null || years < 0) return "No especificado";

        if (years == 0) return "Recién graduado";
        else if (years < 2) return "Junior";
        else if (years < 5) return "Intermedio";
        else if (years < 10) return "Senior";
        else return "Experto";
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return fullName; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getPhone() { return phone; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getSpecialization() { return specialization; }
    public String getWorkplace() { return workplace; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public String getBiography() { return biography; }
    public String getExperienceLevel() { return experienceLevel; }

    // Método para obtener edad calculada
    public Integer getAge() {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // Método para verificar si el perfil profesional está completo
    public Boolean isProfessionalProfileComplete() {
        return licenseNumber != null && !licenseNumber.trim().isEmpty() &&
                specialization != null && !specialization.trim().isEmpty() &&
                workplace != null && !workplace.trim().isEmpty();
    }

    // Método para verificar si es nutricionista experimentado
    public Boolean isExperienced() {
        return yearsOfExperience != null && yearsOfExperience >= 5;
    }
}
