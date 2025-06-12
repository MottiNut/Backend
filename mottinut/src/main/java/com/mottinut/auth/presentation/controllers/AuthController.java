package com.mottinut.auth.presentation.controllers;

import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.AuthService;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.auth.infrastructure.security.CustomUserPrincipal;
import com.mottinut.auth.infrastructure.security.JwtTokenProvider;
import com.mottinut.auth.presentation.dto.*;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.presentation.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthService authService, UserService userService, JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register/patient")
    public ResponseEntity<ApiResponse<LoginResponse>> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        Patient patient = authService.registerPatient(
                new Email(request.getEmail()),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getPhone(),
                request.getHeight(),
                request.getWeight(),
                request.isHasMedicalCondition(),
                request.getChronicDisease(),
                request.getAllergies(),
                request.getDietaryPreferences()
        );

        var token = tokenProvider.generateToken(patient.getUserId(), patient.getRole());

        LoginResponse response = new LoginResponse(
                token.getValue(),
                patient.getUserId().getValue(),
                patient.getRole().getValue(),
                patient.getFullName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Paciente registrado exitosamente", response));
    }

    @PostMapping("/register/nutritionist")
    public ResponseEntity<ApiResponse<LoginResponse>> registerNutritionist(@Valid @RequestBody RegisterNutritionistRequest request) {
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

        var token = tokenProvider.generateToken(nutritionist.getUserId(), nutritionist.getRole());

        LoginResponse response = new LoginResponse(
                token.getValue(),
                nutritionist.getUserId().getValue(),
                nutritionist.getRole().getValue(),
                nutritionist.getFullName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Nutricionista registrado exitosamente", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.authenticate(
                new Email(request.getEmail()),
                request.getPassword()
        );

        var token = tokenProvider.generateToken(user.getUserId(), user.getRole());

        LoginResponse response = new LoginResponse(
                token.getValue(),
                user.getUserId().getValue(),
                user.getRole().getValue(),
                user.getFullName()
        );

        return ResponseEntity.ok(new ApiResponse<>("Login exitoso", response));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Object>> getProfile(@AuthenticationPrincipal CustomUserPrincipal principal) {
        User user = principal.getUser();

        Object response;
        if (user instanceof Patient) {
            response = PatientResponse.fromPatient((Patient) user);
        } else if (user instanceof Nutritionist) {
            response = NutritionistResponse.fromNutritionist((Nutritionist) user);
        } else {
            throw new IllegalStateException("Tipo de usuario no soportado");
        }

        return ResponseEntity.ok(new ApiResponse<>("Perfil obtenido exitosamente", response));
    }
}
