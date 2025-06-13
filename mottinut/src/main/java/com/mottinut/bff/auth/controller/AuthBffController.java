package com.mottinut.bff.auth.controller;

import com.mottinut.bff.auth.dto.request.*;

import com.mottinut.bff.auth.dto.response.AuthResponse;
import com.mottinut.bff.auth.dto.response.NutritionistProfileResponse;
import com.mottinut.bff.auth.dto.response.PatientProfileResponse;
import com.mottinut.bff.auth.dto.response.UserProfileResponse;
import com.mottinut.bff.auth.service.AuthBffService;
import com.mottinut.crosscutting.security.CustomUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bff/auth")
@CrossOrigin(origins = "*")
public class AuthBffController {

    private final AuthBffService authBffService;

    public AuthBffController(AuthBffService authBffService) {
        this.authBffService = authBffService;
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
}
