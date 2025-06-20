package com.mottinut.bff.auth.controller;

import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.bff.auth.dto.request.*;

import com.mottinut.bff.auth.dto.response.AuthResponse;
import com.mottinut.bff.auth.dto.response.NutritionistProfileResponse;
import com.mottinut.bff.auth.dto.response.PatientProfileResponse;
import com.mottinut.bff.auth.dto.response.UserProfileResponse;
import com.mottinut.bff.auth.service.AuthBffService;
import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bff/auth")
@CrossOrigin(origins = "*")
public class AuthBffController {

    private final AuthBffService authBffService;
    private final UserService userService;

    public AuthBffController(AuthBffService authBffService, UserService userService) {
        this.authBffService = authBffService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authBffService.login(request));
    }

    @PostMapping("/register/patient")
    public ResponseEntity<AuthResponse> registerPatient(@Valid @RequestBody RegisterPatientRequest request) {
        return ResponseEntity.ok(authBffService.registerPatient(request));
    }

    @PostMapping("/register/nutritionist")
    public ResponseEntity<AuthResponse> registerNutritionist(@Valid @RequestBody RegisterNutritionistRequest request) {
        return ResponseEntity.ok(authBffService.registerNutritionist(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal CustomUserPrincipal principal) {
        return ResponseEntity.ok(authBffService.getCurrentUserProfile(principal.getUser()));
    }

    @PutMapping("/profile/patient")
    public ResponseEntity<PatientProfileResponse> updatePatientProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdatePatientProfileRequest request) {
        return ResponseEntity.ok(authBffService.updatePatientProfile(principal.getUser().getUserId(), request));
    }

    @PutMapping("/profile/nutritionist")
    public ResponseEntity<NutritionistProfileResponse> updateNutritionistProfile(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody UpdateNutritionistProfileRequest request) {
        return ResponseEntity.ok(authBffService.updateNutritionistProfile(principal.getUser().getUserId(), request));
    }

    @PostMapping("/profile/patient/image")
    public ResponseEntity<PatientProfileResponse> updatePatientProfileImage(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam("image") MultipartFile image) {

        // Validaciones
        if (image.isEmpty()) {
            throw new ValidationException("La imagen no puede estar vacía");
        }

        if (image.getSize() > 5 * 1024 * 1024) { // 5MB máximo
            throw new ValidationException("La imagen no puede superar los 5MB");
        }

        String contentType = image.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new ValidationException("El archivo debe ser una imagen");
        }

        return ResponseEntity.ok(authBffService.updatePatientProfileImage(
                principal.getUser().getUserId(), image));
    }

    @GetMapping("/profile/patient/{userId}/image")
    public ResponseEntity<byte[]> getPatientProfileImage(@PathVariable Long userId) {
        Patient patient = userService.getPatientById(new UserId(userId));

        if (patient.getProfileImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(patient.getImageContentType()))
                .body(patient.getProfileImage());
    }

}
