package com.mottinut.bff.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.mottinut.auth.domain.entities.Nutritionist;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Base64;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("nutritionist")
public class NutritionistProfileResponse extends UserProfileResponse {

    // Información profesional requerida
    private String cnpCode;  // Cambiado de licenseNumber a cnpCode
    private String specialty;  // Cambiado de specialization a specialty
    private String location;   // Cambiado de workplace a location
    private String address;    // Nuevo campo agregado

    // Información profesional opcional
    private String masterDegree;        // Nuevo campo
    private String otherSpecialty;      // Nuevo campo
    private Integer yearsOfExperience;
    private String biography;
    private boolean acceptTerms;        // Nuevo campo

    // Imágenes del carnet (codificadas en Base64)
    private String licenseFrontImageBase64;
    private String licenseBackImageBase64;

    // Información adicional calculada para el frontend
    private Boolean isExperienced;

    // Campos de verificación heredados del User
    private boolean emailVerified;
    private boolean phoneVerified;
    private boolean fullyVerified;
    private String emailVerifiedAt;
    private String phoneVerifiedAt;

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

        // Imagen de perfil en base64 (si existe en UserProfileResponse)
        if (nutritionist.getProfileImage() != null &&
                response.getClass().getSuperclass().getDeclaredFields().length > 0) {
            // Solo si UserProfileResponse tiene el campo profileImageBase64
            // response.setProfileImageBase64(Base64.getEncoder().encodeToString(nutritionist.getProfileImage()));
        }

        // Campos específicos del nutricionista actualizados
        response.setCnpCode(nutritionist.getCnpCode());
        response.setSpecialty(nutritionist.getSpecialty());
        response.setLocation(nutritionist.getLocation());
        response.setAddress(nutritionist.getAddress());
        response.setMasterDegree(nutritionist.getMasterDegree());
        response.setOtherSpecialty(nutritionist.getOtherSpecialty());
        response.setYearsOfExperience(nutritionist.getYearsOfExperience());
        response.setBiography(nutritionist.getBiography());
        response.setAcceptTerms(nutritionist.isAcceptTerms());

        // Imágenes del carnet (en base64)
        response.setLicenseFrontImageBase64(
                nutritionist.getLicenseFrontImage() != null ?
                        Base64.getEncoder().encodeToString(nutritionist.getLicenseFrontImage()) :
                        null
        );
        response.setLicenseBackImageBase64(
                nutritionist.getLicenseBackImage() != null ?
                        Base64.getEncoder().encodeToString(nutritionist.getLicenseBackImage()) :
                        null
        );

        // Campos de verificación
        response.setEmailVerified(nutritionist.isEmailVerified());
        response.setPhoneVerified(nutritionist.isPhoneVerified());
        response.setFullyVerified(nutritionist.isFullyVerified());

        // Fechas de verificación formateadas
        response.setEmailVerifiedAt(
                nutritionist.getEmailVerifiedAt() != null ?
                        nutritionist.getEmailVerifiedAt().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                        null
        );
        response.setPhoneVerifiedAt(
                nutritionist.getPhoneVerifiedAt() != null ?
                        nutritionist.getPhoneVerifiedAt().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                        null
        );

        // Campos calculados
        response.setIsExperienced(nutritionist.isExperienced());

        return response;
    }

    // Métodos de utilidad adicionales si necesitas calcular experienceLevel en el frontend
    public String getCalculatedExperienceLevel() {
        return calculateExperienceLevel(this.yearsOfExperience);
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