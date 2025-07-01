package com.mottinut.bff.nutritionplan.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.bff.nutritionplan.dto.request.*;
import com.mottinut.bff.nutritionplan.dto.response.NutritionPlanResponseDto;
import com.mottinut.bff.nutritionplan.dto.response.PendingPlanResponseDto;
import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.notification.domain.services.NotificationDomainService;
import com.mottinut.notification.domain.valueobjects.NotificationContent;
import com.mottinut.nutritionplan.domain.entities.NutritionPlan;
import com.mottinut.nutritionplan.domain.enums.ReviewAction;
import com.mottinut.nutritionplan.domain.services.NutritionPlanService;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NutritionistNutritionPlanService {

    private final NutritionPlanService nutritionPlanService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final NotificationDomainService notificationDomainService; // Cambio aquí

    public NutritionistNutritionPlanService(NutritionPlanService nutritionPlanService,
                                            UserService userService,
                                            ObjectMapper objectMapper,
                                            NotificationDomainService notificationDomainService) { // Cambio aquí
        this.nutritionPlanService = nutritionPlanService;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.notificationDomainService = notificationDomainService; // Cambio aquí
    }

    public NutritionPlanResponseDto generatePlan(GeneratePlanRequestDto request, Authentication authentication) {
        try {
            UserId nutritionistId = getCurrentUserId(authentication);
            UserId patientId = new UserId(request.getPatientUserId());
            LocalDate weekStartDate = LocalDate.parse(request.getWeekStartDate());

            NutritionPlan plan = nutritionPlanService.generatePlan(nutritionistId, patientId, weekStartDate,
                    request.getEnergyRequirement(), request.getGoal(), request.getSpecialRequirements(),
                    request.getMealsPerDay());

            return buildPlanResponse(plan);
        } catch (IllegalStateException e) {
            throw new ValidationException("MEDICAL_HISTORY_REQUIRED");
        } catch (NotFoundException e) {
            throw new ValidationException("RESOURCE_NOT_FOUND");
        } catch (Exception e) {
            throw new RuntimeException("Error interno del servidor al generar el plan nutricional", e);
        }
    }

    public List<PendingPlanResponseDto> getPendingPlans(Authentication authentication) {
        UserId nutritionistId = getCurrentUserId(authentication);
        List<NutritionPlan> pendingPlans = nutritionPlanService.getPendingPlansByNutritionist(nutritionistId);
        return pendingPlans.stream().map(this::buildPendingPlanResponse).collect(Collectors.toList());
    }

    public DetailedNutritionPlanDto getPlanDetails(Long planId, Authentication authentication) {
        UserId nutritionistId = getCurrentUserId(authentication);
        NutritionPlan plan = nutritionPlanService.getPlanByIdForNutritionist(new NutritionPlanId(planId), nutritionistId);
        return buildDetailedPlanResponse(plan);
    }

    public NutritionPlanResponseDto reviewPlan(Long planId, ReviewPlanRequestDto request, Authentication authentication) {
        try {
            UserId nutritionistId = getCurrentUserId(authentication);
            NutritionPlanId nutritionPlanId = new NutritionPlanId(planId);
            ReviewAction action = ReviewAction.fromString(request.getAction());

            NutritionPlan reviewedPlan = nutritionPlanService.reviewPlan(nutritionistId, nutritionPlanId, action, request.getReviewNotes());
            User patient = userService.getUserById(reviewedPlan.getPatientId());

            // Enviar notificaciones usando el nuevo servicio
            if (action == ReviewAction.APPROVE) {
                sendPlanApprovedNotification(reviewedPlan.getPatientId(), patient.getFullName(), planId);
            } else if (action == ReviewAction.REJECT) {
                sendPlanRejectedNotification(reviewedPlan.getPatientId(), patient.getFullName(), planId, request.getReviewNotes());
            }

            return buildPlanResponse(reviewedPlan);
        } catch (Exception e) {
            throw new RuntimeException("Error revisando el plan: " + e.getMessage(), e);
        }
    }

    public NutritionPlanResponseDto editPlan(Long planId, EditPlanRequestDto request, Authentication authentication) {
        try {
            UserId nutritionistId = getCurrentUserId(authentication);
            NutritionPlanId nutritionPlanId = new NutritionPlanId(planId);

            if (request.getPlanContent() == null) {
                throw new IllegalArgumentException("El contenido del plan no puede ser nulo");
            }

            String planContentString = serializePlanContent(request.getPlanContent());
            NutritionPlan editedPlan = nutritionPlanService.editPlan(nutritionistId, nutritionPlanId, planContentString, request.getReviewNotes());

            return buildPlanResponse(editedPlan);
        } catch (Exception e) {
            throw new RuntimeException("Error editando el plan: " + e.getMessage(), e);
        }
    }

    public List<RejectedByPatientDto> getRejectedByPatientPlans(Authentication authentication) {
        UserId nutritionistId = getCurrentUserId(authentication);
        List<NutritionPlan> rejectedPlans = nutritionPlanService.getRejectedByPatientPlans(nutritionistId);
        return rejectedPlans.stream().map(this::buildRejectedByPatientResponse).collect(Collectors.toList());
    }

    // Métodos privados para enviar notificaciones
    private void sendPlanApprovedNotification(UserId patientId, String patientName, Long planId) {
        try {
            NotificationContent content = NotificationContent.builder()
                    .title("Plan Nutricional Aprobado")
                    .body(String.format("Tu plan nutricional ha sido aprobado. ¡Ya puedes comenzar a seguirlo!"))
                    .data(java.util.Map.of(
                            "type", "PLAN_APPROVED",
                            "planId", planId.toString(),
                            "patientName", patientName
                    ))
                    .build();

            notificationDomainService.sendNotification(patientId, content);
        } catch (Exception e) {
            // Log del error pero no fallar la operación principal
            System.err.println("Error enviando notificación de plan aprobado: " + e.getMessage());
        }
    }

    private void sendPlanRejectedNotification(UserId patientId, String patientName, Long planId, String reviewNotes) {
        try {
            NotificationContent content = NotificationContent.builder()
                    .title("Plan Nutricional Requiere Revisión")
                    .body(String.format("Tu plan nutricional necesita ajustes. Revisa los comentarios del nutricionista."))
                    .data(java.util.Map.of(
                            "type", "PLAN_REJECTED",
                            "planId", planId.toString(),
                            "patientName", patientName,
                            "reviewNotes", reviewNotes != null ? reviewNotes : ""
                    ))
                    .build();

            notificationDomainService.sendNotification(patientId, content);
        } catch (Exception e) {
            // Log del error pero no fallar la operación principal
            System.err.println("Error enviando notificación de plan rechazado: " + e.getMessage());
        }
    }

    // Resto de métodos sin cambios...
    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }

    private String serializePlanContent(Object planContent) {
        try {
            if (planContent instanceof String) {
                String contentStr = (String) planContent;
                objectMapper.readTree(contentStr);
                return contentStr;
            } else {
                return objectMapper.writeValueAsString(planContent);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error al procesar el contenido del plan: " + e.getMessage(), e);
        }
    }

    private Object parsePlanContent(String planContent) {
        if (planContent == null || planContent.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(planContent, Object.class);
        } catch (JsonProcessingException e) {
            String cleanContent = planContent.trim();
            if (cleanContent.startsWith("{") && cleanContent.endsWith("}")) {
                try {
                    return objectMapper.readValue(cleanContent, Object.class);
                } catch (JsonProcessingException e2) {
                    return planContent;
                }
            }
            return planContent;
        }
    }

    private NutritionPlanResponseDto buildPlanResponse(NutritionPlan plan) {
        User patient = userService.getUserById(plan.getPatientId());
        User nutritionist = userService.getUserById(plan.getNutritionistId());
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