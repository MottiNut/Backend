package com.mottinut.auth.domain.entities;

import com.mottinut.auth.domain.valueobjects.LicenseNumber;
import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.validation.ValidationException;
import lombok.Getter;

import java.time.LocalDate;

public class Nutritionist extends User {
    @Getter
    private final String cnpCode;

    @Getter
    private final byte[] licenseFrontImage;

    @Getter
    private final byte[] licenseBackImage;

    @Getter
    private final String specialty;

    @Getter
    private final String masterDegree; // Puede ser null

    @Getter
    private final String otherSpecialty; // Puede ser null

    @Getter
    private final String location;

    @Getter
    private final String address;

    @Getter
    private final boolean acceptTerms;

    // Campos adicionales que pueden ser útiles mantener
    @Getter
    private Integer yearsOfExperience;

    @Getter
    private String biography;

    public Nutritionist(UserId userId, Email email, Password password,
                        String firstName, String lastName, LocalDate birthDate, String phone,
                        byte[] profileImage, String imageContentType,
                        String cnpCode, byte[] licenseFrontImage, byte[] licenseBackImage,
                        String specialty, String masterDegree, String otherSpecialty,
                        String location, String address, boolean acceptTerms) {

        super(userId, email, password, Role.NUTRITIONIST, firstName, lastName, birthDate, phone);

        // Validaciones
        if (profileImage == null || imageContentType == null) {
            throw new ValidationException("La imagen de perfil es obligatoria para nutricionistas");
        }
        if (cnpCode == null || cnpCode.trim().isEmpty()) {
            throw new ValidationException("El código CNP es requerido");
        }
        if (licenseFrontImage == null || licenseBackImage == null) {
            throw new ValidationException("Las imágenes del carnet de colegiatura son obligatorias");
        }
        if (specialty == null || specialty.trim().isEmpty()) {
            throw new ValidationException("La especialidad es requerida");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new ValidationException("La ubicación es requerida");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException("La dirección es requerida");
        }
        if (!acceptTerms) {
            throw new ValidationException("Debe aceptar los términos y condiciones");
        }

        // Asignación de campos
        this.profileImage = profileImage;
        this.imageContentType = imageContentType;
        this.cnpCode = cnpCode;
        this.licenseFrontImage = licenseFrontImage;
        this.licenseBackImage = licenseBackImage;
        this.specialty = specialty;
        this.masterDegree = masterDegree; // Puede ser null
        this.otherSpecialty = otherSpecialty; // Puede ser null
        this.location = location;
        this.address = address;
        this.acceptTerms = acceptTerms;
    }

    // Constructor alternativo si quieres mantener compatibilidad con código existente
    public Nutritionist(UserId userId, Email email, Password password,
                        String firstName, String lastName, LocalDate birthDate, String phone,
                        byte[] profileImage, String imageContentType,
                        String cnpCode, byte[] licenseFrontImage, byte[] licenseBackImage,
                        String specialty, String masterDegree, String otherSpecialty,
                        String location, String address, boolean acceptTerms,
                        Integer yearsOfExperience, String biography) {

        this(userId, email, password, firstName, lastName, birthDate, phone,
                profileImage, imageContentType, cnpCode, licenseFrontImage, licenseBackImage,
                specialty, masterDegree, otherSpecialty, location, address, acceptTerms);

        this.yearsOfExperience = yearsOfExperience;
        this.biography = biography;
    }

    public void updateProfessionalProfile(Integer yearsOfExperience, String biography) {
        this.yearsOfExperience = yearsOfExperience;
        this.biography = biography;
    }

    public boolean isExperienced() {
        return yearsOfExperience != null && yearsOfExperience >= 5;
    }

    public boolean hasMasterDegree() {
        return masterDegree != null && !masterDegree.trim().isEmpty();
    }

    public boolean hasOtherSpecialty() {
        return otherSpecialty != null && !otherSpecialty.trim().isEmpty();
    }
}