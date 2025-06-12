package com.mottinut.auth.domain.entities;

import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.validation.ValidationException;
import lombok.Getter;

import java.time.LocalDate;

public class Nutritionist extends User {
    @Getter
    private final String licenseNumber; // Número de colegiatura (requerido)
    @Getter
    private final String specialization; // Especialidad (requerido)
    @Getter
    private final String workplace; // Donde trabaja (requerido)
    @Getter
    private Integer yearsOfExperience; // Opcional
    @Getter
    private String biography; // Opcional

    public Nutritionist(UserId userId, Email email, Password password,
                        String firstName, String lastName, LocalDate birthDate, String phone,
                        String licenseNumber, String specialization, String workplace,
                        Integer yearsOfExperience, String biography) {
        super(userId, email, password, Role.NUTRITIONIST, firstName, lastName, birthDate, phone);

        if (licenseNumber == null || licenseNumber.trim().isEmpty()) {
            throw new ValidationException("El número de colegiatura es requerido");
        }
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new ValidationException("La especialidad es requerida");
        }
        if (workplace == null || workplace.trim().isEmpty()) {
            throw new ValidationException("El lugar de trabajo es requerido");
        }

        this.licenseNumber = licenseNumber;
        this.specialization = specialization;
        this.workplace = workplace;
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
}