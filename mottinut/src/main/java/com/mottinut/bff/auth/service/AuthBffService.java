package com.mottinut.bff.auth.service;

import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.AuthService;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.auth.domain.valueobjects.Token;
import com.mottinut.bff.auth.dto.request.*;
import com.mottinut.bff.auth.dto.response.AuthResponse;
import com.mottinut.bff.auth.dto.response.NutritionistProfileResponse;
import com.mottinut.bff.auth.dto.response.PatientProfileResponse;
import com.mottinut.bff.auth.dto.response.UserProfileResponse;
import com.mottinut.crosscutting.security.JwtTokenProvider;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthBffService {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthBffService(AuthService authService, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse login(LoginRequest request) {
        User user = authService.authenticate(new Email(request.getEmail()), request.getPassword());
        Token token = jwtTokenProvider.generateToken(user.getUserId(), user.getRole());

        return AuthResponse.builder()
                .token(token.getValue())
                .userId(user.getUserId().getValue())
                .role(user.getRole().getValue())
                .fullName(user.getFullName())
                .email(user.getEmail().getValue())
                .build();
    }

    public AuthResponse registerPatient(RegisterPatientRequest request) {
        Patient patient = authService.registerPatient(
                new Email(request.getEmail()),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getPhone(),
                request.getHeight(),
                request.getWeight(),
                request.getHasMedicalCondition(),
                request.getChronicDisease(),
                request.getAllergies(),
                request.getDietaryPreferences()
        );

        Token token = jwtTokenProvider.generateToken(patient.getUserId(), patient.getRole());

        return AuthResponse.builder()
                .token(token.getValue())
                .userId(patient.getUserId().getValue())
                .role(patient.getRole().getValue())
                .fullName(patient.getFullName())
                .email(patient.getEmail().getValue())
                .build();
    }

    public AuthResponse registerNutritionist(RegisterNutritionistRequest request) {
        Nutritionist nutritionist = authService.registerNutritionist(
                new Email(request.getEmail()),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getPhone(),
                request.getLicenseNumber(),
                request.getSpecialization(),
                request.getWorkplace()
        );

        Token token = jwtTokenProvider.generateToken(nutritionist.getUserId(), nutritionist.getRole());

        return AuthResponse.builder()
                .token(token.getValue())
                .userId(nutritionist.getUserId().getValue())
                .role(nutritionist.getRole().getValue())
                .fullName(nutritionist.getFullName())
                .email(nutritionist.getEmail().getValue())
                .build();
    }

    public UserProfileResponse getCurrentUserProfile(User user) {
        if (user.getRole().isPatient()) {
            Patient patient = userService.getPatientById(user.getUserId());
            return PatientProfileResponse.fromPatient(patient);
        } else if (user.getRole().isNutritionist()) {
            Nutritionist nutritionist = userService.getNutritionistById(user.getUserId());
            return NutritionistProfileResponse.fromNutritionist(nutritionist);
        }
        throw new ValidationException("Tipo de usuario no válido");
    }

    public PatientProfileResponse updatePatientProfile(UserId userId, @Valid UpdatePatientProfileRequest request) {
        Patient updatedPatient = userService.updatePatientProfile(
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getHeight(),
                request.getWeight(),
                request.getHasMedicalCondition(),
                request.getChronicDisease(),
                request.getAllergies(),
                request.getDietaryPreferences(),
                request.getEmergencyContact()
        );

        return PatientProfileResponse.fromPatient(updatedPatient);
    }

    public NutritionistProfileResponse updateNutritionistProfile(UserId userId, @Valid UpdateNutritionistProfileRequest request) {
        Nutritionist updatedNutritionist = userService.updateNutritionistProfile(
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getYearsOfExperience(),
                request.getBiography()
        );

        return NutritionistProfileResponse.fromNutritionist(updatedNutritionist);
    }
}
