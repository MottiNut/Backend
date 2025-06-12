package com.mottinut.auth.presentation.controllers;

import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.auth.infrastructure.security.CustomUserPrincipal;
import com.mottinut.auth.presentation.dto.NutritionistResponse;
import com.mottinut.auth.presentation.dto.PatientResponse;
import com.mottinut.auth.presentation.dto.UpdateNutritionistProfileRequest;
import com.mottinut.auth.presentation.dto.UpdatePatientProfileRequest;
import com.mottinut.shared.domain.valueobjects.UserId;
import com.mottinut.shared.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/patient")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatientProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdatePatientProfileRequest request) {

        // Verificar que el usuario es un paciente
        if (!principal.getUser().getRole().isPatient()) {
            throw new ValidationException("Solo los pacientes pueden actualizar este perfil");
        }

        Patient updatedPatient = userService.updatePatientProfile(
                principal.getUser().getUserId(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getHeight(),
                request.getWeight(),
                request.getHasMedicalCondition() != null ? request.getHasMedicalCondition() : false,
                request.getChronicDisease(),
                request.getAllergies(),
                request.getDietaryPreferences(),
                request.getEmergencyContact()
        );

        PatientResponse response = PatientResponse.fromPatient(updatedPatient);
        return ResponseEntity.ok(new ApiResponse<>("Perfil de paciente actualizado exitosamente", response));
    }

    @PutMapping("/nutritionist")
    public ResponseEntity<ApiResponse<NutritionistResponse>> updateNutritionistProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdateNutritionistProfileRequest request) {

        // Verificar que el usuario es un nutricionista
        if (!principal.getUser().getRole().isNutritionist()) {
            throw new ValidationException("Solo los nutricionistas pueden actualizar este perfil");
        }

        Nutritionist updatedNutritionist = userService.updateNutritionistProfile(
                principal.getUser().getUserId(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhone(),
                request.getYearsOfExperience(),
                request.getBiography()
        );

        NutritionistResponse response = NutritionistResponse.fromNutritionist(updatedNutritionist);
        return ResponseEntity.ok(new ApiResponse<>("Perfil de nutricionista actualizado exitosamente", response));
    }

    @GetMapping("/patient/{userId}")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientProfile(@PathVariable Long userId) {
        Patient patient = userService.getPatientById(new UserId(userId));
        PatientResponse response = PatientResponse.fromPatient(patient);
        return ResponseEntity.ok(new ApiResponse<>("Perfil de paciente obtenido exitosamente", response));
    }

    @GetMapping("/nutritionist/{userId}")
    public ResponseEntity<ApiResponse<NutritionistResponse>> getNutritionistProfile(@PathVariable Long userId) {
        Nutritionist nutritionist = userService.getNutritionistById(new UserId(userId));
        NutritionistResponse response = NutritionistResponse.fromNutritionist(nutritionist);
        return ResponseEntity.ok(new ApiResponse<>("Perfil de nutricionista obtenido exitosamente", response));
    }
}