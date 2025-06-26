package com.mottinut.auth.domain.entities;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.mottinut.auth.domain.valueobjects.LicenseNumber;
import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.bff.auth.dto.request.RegisterPatientRequest;
import com.mottinut.bff.auth.dto.request.UpdateNutritionistProfileRequest;
import com.mottinut.bff.auth.dto.request.UpdatePatientProfileRequest;
import com.mottinut.bff.auth.dto.response.PatientProfileResponse;
import com.mottinut.bff.auth.dto.response.UserProfileResponse;
import com.mottinut.crosscutting.security.JwtTokenProvider;
import com.mottinut.shared.domain.exceptions.BusinessException;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Pattern;

public class Patient extends User {
    @Getter
    private Double height;
    @Getter
    private Double weight;
    private boolean hasMedicalCondition;
    @Getter
    private String chronicDisease;
    @Getter
    private String allergies;
    @Getter
    private String dietaryPreferences;
    @Getter
    private String emergencyContact;
    @Getter
    private String gender;

    public Patient(UserId userId, Email email, Password password, String firstName, String lastName,
                   LocalDate birthDate, String phone, Double height, Double weight,
                   boolean hasMedicalCondition, String chronicDisease, String allergies,
                   String dietaryPreferences, String emergencyContact, String gender) {
        super(userId, email, password, Role.PATIENT, firstName, lastName, birthDate, phone);
        this.height = height;
        this.weight = weight;
        this.hasMedicalCondition = hasMedicalCondition;
        this.chronicDisease = chronicDisease;
        this.allergies = allergies;
        this.dietaryPreferences = dietaryPreferences;
        this.emergencyContact = emergencyContact;
        this.gender = gender;
    }

    public boolean hasMedicalCondition() {
        return hasMedicalCondition;
    }

    public void updateMedicalProfile(Double height, Double weight, boolean hasMedicalCondition,
                                     String chronicDisease, String allergies, String dietaryPreferences,
                                     String emergencyContact, String gender) {
        this.height = height;
        this.weight = weight;
        this.hasMedicalCondition = hasMedicalCondition;
        this.chronicDisease = chronicDisease;
        this.allergies = allergies;
        this.dietaryPreferences = dietaryPreferences;
        this.emergencyContact = emergencyContact;
        this.gender = gender;
    }

    public double calculateBMI() {
        if (height == null || weight == null || height <= 0) {
            throw new IllegalStateException("Altura y peso son requeridos para calcular el BMI");
        }
        return weight / Math.pow(height / 100, 2);
    }
}
