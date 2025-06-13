package com.mottinut.nutritionplan.presentation.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.nutritionplan.domain.entities.NutritionPlan;
import com.mottinut.nutritionplan.domain.enums.ReviewAction;
import com.mottinut.nutritionplan.domain.services.NutritionPlanService;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.nutritionplan.presentation.dto.request.*;
import com.mottinut.nutritionplan.presentation.dto.response.NutritionPlanResponseDto;
import com.mottinut.nutritionplan.presentation.dto.response.PendingPlanResponseDto;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final ObjectMapper objectMapper;

    public AiPlanController(NutritionPlanService nutritionPlanService, UserService userService, ObjectMapper objectMapper) {
        this.nutritionPlanService = nutritionPlanService;
        this.userService = userService;
        this.objectMapper = objectMapper;
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

    @GetMapping("/plan/{planId}")
    public ResponseEntity<DetailedNutritionPlanDto> getPlanDetails(@PathVariable Long planId) {
        NutritionPlan plan = nutritionPlanService.getPlanById(new NutritionPlanId(planId));
        DetailedNutritionPlanDto response = buildDetailedPlanResponse(plan);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/review-plan/{planId}")
    public ResponseEntity<NutritionPlanResponseDto> reviewPlan(
            @PathVariable Long planId,
            @Valid @RequestBody ReviewPlanRequestDto request,
            Authentication authentication) {

        UserId nutritionistId = getCurrentUserId(authentication);
        NutritionPlanId nutritionPlanId = new NutritionPlanId(planId);
        ReviewAction action = ReviewAction.fromString(request.getAction());

        NutritionPlan reviewedPlan = nutritionPlanService.reviewPlan(
                nutritionistId, nutritionPlanId, action, request.getReviewNotes());

        NutritionPlanResponseDto response = buildPlanResponse(reviewedPlan);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit-plan/{planId}")
    public ResponseEntity<NutritionPlanResponseDto> editPlan(
            @PathVariable Long planId,
            @Valid @RequestBody EditPlanRequestDto request,
            Authentication authentication) {

        try {
            UserId nutritionistId = getCurrentUserId(authentication);
            NutritionPlanId nutritionPlanId = new NutritionPlanId(planId);

            // Validar que planContent no sea null
            if (request.getPlanContent() == null) {
                throw new IllegalArgumentException("El contenido del plan no puede ser nulo");
            }

            // Convertir Object a String JSON de manera más robusta
            String planContentString = serializePlanContent(request.getPlanContent());

            NutritionPlan editedPlan = nutritionPlanService.editPlan(
                    nutritionistId, nutritionPlanId, planContentString, request.getReviewNotes());

            NutritionPlanResponseDto response = buildPlanResponse(editedPlan);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error procesando el contenido del plan: " + e.getMessage());
        }
    }

    @GetMapping("/rejected-by-patient")
    public ResponseEntity<List<RejectedByPatientDto>> getRejectedByPatientPlans(Authentication authentication) {
        UserId nutritionistId = getCurrentUserId(authentication);
        List<NutritionPlan> rejectedPlans = nutritionPlanService.getRejectedByPatientPlans(nutritionistId);

        List<RejectedByPatientDto> response = rejectedPlans.stream()
                .map(this::buildRejectedByPatientResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }

    /**
     * Serializa el contenido del plan de manera robusta
     */
    private String serializePlanContent(Object planContent) {
        try {
            if (planContent instanceof String) {
                // Si ya es String, verificar si es JSON válido
                String contentStr = (String) planContent;
                // Intentar parsearlo para validar que es JSON válido
                objectMapper.readTree(contentStr);
                return contentStr;
            } else {
                // Si es un objeto, serializarlo a JSON
                return objectMapper.writeValueAsString(planContent);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error al procesar el contenido del plan: " + e.getMessage(), e);
        }
    }

    /**
     * Parsea el contenido del plan de manera robusta
     */
    private Object parsePlanContent(String planContent) {
        if (planContent == null || planContent.trim().isEmpty()) {
            return null;
        }

        try {
            // Configurar ObjectMapper para ser más flexible
            return objectMapper.readValue(planContent, Object.class);
        } catch (JsonProcessingException e) {
            // Log del error para debugging
            System.err.println("Error parsing plan content: " + e.getMessage());
            System.err.println("Content: " + planContent);

            // Si falla el parsing, intentar limpiar el string
            String cleanContent = planContent.trim();
            if (cleanContent.startsWith("{") && cleanContent.endsWith("}")) {
                try {
                    return objectMapper.readValue(cleanContent, Object.class);
                } catch (JsonProcessingException e2) {
                    // Si aún falla, devolver como string
                    return planContent;
                }
            }
            return planContent;
        }
    }

    private NutritionPlanResponseDto buildPlanResponse(NutritionPlan plan) {
        User patient = userService.getUserById(plan.getPatientId());
        User nutritionist = userService.getUserById(plan.getNutritionistId());

        // Parsear el planContent como Object para devolverlo como JSON estructurado
        Object parsedPlanContent = parsePlanContent(plan.getPlanContent());

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
                .planContent(parsedPlanContent)
                .status(plan.getStatus().getValue())
                .reviewNotes(plan.getReviewNotes())
                .createdAt(plan.getCreatedAt().toString())
                .reviewedAt(plan.getReviewedAt() != null ? plan.getReviewedAt().toString() : null)
                .build();
    }

    private DetailedNutritionPlanDto buildDetailedPlanResponse(NutritionPlan plan) {
        User patient = userService.getUserById(plan.getPatientId());
        User nutritionist = userService.getUserById(plan.getNutritionistId());

        Object parsedPlanContent = parsePlanContent(plan.getPlanContent());

        return DetailedNutritionPlanDto.builder()
                .planId(plan.getPlanId().getValue())
                .patientId(plan.getPatientId().getValue())
                .patientName(patient.getFullName())
                .nutritionistId(plan.getNutritionistId().getValue())
                .nutritionistName(nutritionist.getFullName())
                .weekStartDate(plan.getWeekStartDate().toString())
                .energyRequirement(plan.getEnergyRequirement())
                .goal(plan.getGoal())
                .specialRequirements(plan.getSpecialRequirements())
                .planContent(parsedPlanContent)
                .status(plan.getStatus().getValue())
                .reviewNotes(plan.getReviewNotes())
                .patientFeedback(plan.getPatientFeedback())
                .createdAt(plan.getCreatedAt().toString())
                .reviewedAt(plan.getReviewedAt() != null ? plan.getReviewedAt().toString() : null)
                .patientResponseAt(plan.getPatientResponseAt() != null ? plan.getPatientResponseAt().toString() : null)
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

    private RejectedByPatientDto buildRejectedByPatientResponse(NutritionPlan plan) {
        User patient = userService.getUserById(plan.getPatientId());

        return RejectedByPatientDto.builder()
                .planId(plan.getPlanId().getValue())
                .patientId(plan.getPatientId().getValue())
                .patientName(patient.getFullName())
                .weekStartDate(plan.getWeekStartDate().toString())
                .energyRequirement(plan.getEnergyRequirement())
                .goal(plan.getGoal())
                .patientFeedback(plan.getPatientFeedback())
                .patientResponseAt(plan.getPatientResponseAt() != null ? plan.getPatientResponseAt().toString() : null)
                .build();
    }
}