package com.mottinut.bff.nutritionplan.controller;

import com.mottinut.bff.nutritionplan.dto.request.*;
import com.mottinut.bff.nutritionplan.dto.response.NutritionPlanResponseDto;
import com.mottinut.bff.nutritionplan.dto.response.PendingPlanResponseDto;
import com.mottinut.bff.nutritionplan.service.NutritionistNutritionPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff/nutritionist/nutrition-plans")
@PreAuthorize("hasRole('NUTRITIONIST')")
@CrossOrigin(origins = "*")
public class NutritionistNutritionPlanController {

    private final NutritionistNutritionPlanService nutritionistService;

    public NutritionistNutritionPlanController(NutritionistNutritionPlanService nutritionistService) {
        this.nutritionistService = nutritionistService;
    }

    @PostMapping("/generate")
    public ResponseEntity<NutritionPlanResponseDto> generatePlan(
            @Valid @RequestBody GeneratePlanRequestDto request,
            Authentication authentication) {

        NutritionPlanResponseDto response = nutritionistService.generatePlan(request, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PendingPlanResponseDto>> getPendingPlans(Authentication authentication) {
        List<PendingPlanResponseDto> response = nutritionistService.getPendingPlans(authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<DetailedNutritionPlanDto> getPlanDetails(
            @PathVariable Long planId,
            Authentication authentication) {
        DetailedNutritionPlanDto response = nutritionistService.getPlanDetails(planId, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{planId}/review")
    public ResponseEntity<NutritionPlanResponseDto> reviewPlan(
            @PathVariable Long planId,
            @Valid @RequestBody ReviewPlanRequestDto request,
            Authentication authentication) {

        NutritionPlanResponseDto response = nutritionistService.reviewPlan(planId, request, authentication);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{planId}/edit")
    public ResponseEntity<NutritionPlanResponseDto> editPlan(
            @PathVariable Long planId,
            @Valid @RequestBody EditPlanRequestDto request,
            Authentication authentication) {

        NutritionPlanResponseDto response = nutritionistService.editPlan(planId, request, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rejected-by-patient")
    public ResponseEntity<List<RejectedByPatientDto>> getRejectedByPatientPlans(Authentication authentication) {
        List<RejectedByPatientDto> response = nutritionistService.getRejectedByPatientPlans(authentication);
        return ResponseEntity.ok(response);
    }
}
