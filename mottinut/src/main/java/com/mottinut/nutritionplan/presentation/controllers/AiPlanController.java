package com.mottinut.nutritionplan.presentation.controllers;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.auth.infrastructure.security.CustomUserPrincipal;
import com.mottinut.nutritionplan.domain.entities.NutritionPlan;
import com.mottinut.nutritionplan.domain.enums.ReviewAction;
import com.mottinut.nutritionplan.domain.services.NutritionPlanService;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.nutritionplan.presentation.dto.request.GeneratePlanRequestDto;
import com.mottinut.nutritionplan.presentation.dto.request.ReviewPlanRequestDto;
import com.mottinut.nutritionplan.presentation.dto.response.NutritionPlanResponseDto;
import com.mottinut.nutritionplan.presentation.dto.response.PendingPlanResponseDto;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@PreAuthorize("hasRole('NUTRITIONIST')")
@CrossOrigin(origins = "*")
public class AiPlanController {

    private final NutritionPlanService nutritionPlanService;
    private final UserService userService;

    public AiPlanController(NutritionPlanService nutritionPlanService, UserService userService) {
        this.nutritionPlanService = nutritionPlanService;
        this.userService = userService;
    }

    @PostMapping("/generate-plan")
    public ResponseEntity<NutritionPlanResponseDto> generatePlan(
            @Valid @RequestBody GeneratePlanRequestDto request,
            Authentication authentication) {

        UserId nutritionistId = getCurrentUserId(authentication);
        UserId patientId = new UserId(request.getPatientUserId());
        LocalDate weekStartDate = LocalDate.parse(request.getWeekStartDate());

        NutritionPlan plan = nutritionPlanService.generatePlan(
                nutritionistId, patientId, weekStartDate,
                request.getEnergyRequirement(), request.getGoal(),
                request.getSpecialRequirements());


        NutritionPlanResponseDto response = buildPlanResponse(plan);
        System.out.println("Respuesta desde FastAPI:");
        System.out.println(response.toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-plans")
    public ResponseEntity<List<PendingPlanResponseDto>> getPendingPlans() {
        List<NutritionPlan> pendingPlans = nutritionPlanService.getPendingPlans();

        List<PendingPlanResponseDto> response = pendingPlans.stream()
                .map(this::buildPendingPlanResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/review-plan/{generationId}")
    public ResponseEntity<NutritionPlanResponseDto> reviewPlan(
            @PathVariable Long generationId,
            @Valid @RequestBody ReviewPlanRequestDto request,
            Authentication authentication) {

        UserId nutritionistId = getCurrentUserId(authentication);
        NutritionPlanId planId = new NutritionPlanId(generationId);
        ReviewAction action = ReviewAction.fromString(request.getAction());

        NutritionPlan reviewedPlan = nutritionPlanService.reviewPlan(
                nutritionistId, planId, action, request.getReviewNotes());

        NutritionPlanResponseDto response = buildPlanResponse(reviewedPlan);
        return ResponseEntity.ok(response);
    }

    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }

    private NutritionPlanResponseDto buildPlanResponse(NutritionPlan plan) {
        User patient = userService.getUserById(plan.getPatientId());
        User nutritionist = userService.getUserById(plan.getNutritionistId());

        return NutritionPlanResponseDto.builder()
                .planId(plan.getPlanId().getValue())
                .patientId(plan.getPatientId().getValue())
                .patientName(patient.getFullName())
                .nutritionistId(plan.getNutritionistId().getValue())
                .nutritionistName(nutritionist.getFullName())
                .weekStartDate(plan.getWeekStartDate().toString())
                .energyRequirement(plan.getEnergyRequirement())
                .goal(plan.getGoal())
                .specialRequirements(plan.getSpecialRequirements())
                .planContent(plan.getPlanContent())
                .status(plan.getStatus().getValue())
                .reviewNotes(plan.getReviewNotes())
                .createdAt(plan.getCreatedAt().toString())
                .reviewedAt(plan.getReviewedAt() != null ? plan.getReviewedAt().toString() : null)
                .build();
    }

    private PendingPlanResponseDto buildPendingPlanResponse(NutritionPlan plan) {
        User patient = userService.getUserById(plan.getPatientId());

        return PendingPlanResponseDto.builder()
                .planId(plan.getPlanId().getValue())
                .patientId(plan.getPatientId().getValue())
                .patientName(patient.getFullName())
                .weekStartDate(plan.getWeekStartDate().toString())
                .energyRequirement(plan.getEnergyRequirement())
                .goal(plan.getGoal())
                .specialRequirements(plan.getSpecialRequirements())
                .createdAt(plan.getCreatedAt().toString())
                .build();
    }
}
