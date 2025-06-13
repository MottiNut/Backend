package com.mottinut.bff.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.mottinut.auth.domain.entities.Nutritionist;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("nutritionist")
public class NutritionistProfileResponse extends UserProfileResponse {

    // Información profesional requerida
    private String licenseNumber;
    private String specialization;
    private String workplace;

    // Información profesional opcional
    private Integer yearsOfExperience;
    private String biography;

    // Información adicional calculada para el frontend
    private Boolean isExperienced;
    private String experienceLevel;

    public static NutritionistProfileResponse fromNutritionist(Nutritionist nutritionist) {
        NutritionistProfileResponse response = new NutritionistProfileResponse();

        // Campos heredados de UserProfileResponse
        response.setUserId(nutritionist.getUserId().getValue());
        response.setEmail(nutritionist.getEmail().getValue());
        response.setFirstName(nutritionist.getFirstName());
        response.setLastName(nutritionist.getLastName());
        response.setFullName(nutritionist.getFullName());
        response.setBirthDate(nutritionist.getBirthDate());
        response.setPhone(nutritionist.getPhone());
        response.setRole(nutritionist.getRole().getValue());
        response.setCreatedAt(nutritionist.getCreatedAt());

        // Campos específicos del nutricionista
        response.setLicenseNumber(nutritionist.getLicenseNumber());
        response.setSpecialization(nutritionist.getSpecialization());
        response.setWorkplace(nutritionist.getWorkplace());
        response.setYearsOfExperience(nutritionist.getYearsOfExperience());
        response.setBiography(nutritionist.getBiography());

        // Campos calculados para el frontend
        response.setIsExperienced(nutritionist.isExperienced());
        response.setExperienceLevel(calculateExperienceLevel(nutritionist.getYearsOfExperience()));

        return response;
    }

    private static String calculateExperienceLevel(Integer yearsOfExperience) {
        if (yearsOfExperience == null || yearsOfExperience == 0) {
            return "Recién graduado";
        } else if (yearsOfExperience < 3) {
            return "Junior";
        } else if (yearsOfExperience < 5) {
            return "Intermedio";
        } else if (yearsOfExperience < 10) {
            return "Senior";
        } else {
            return "Experto";
        }
    }
}