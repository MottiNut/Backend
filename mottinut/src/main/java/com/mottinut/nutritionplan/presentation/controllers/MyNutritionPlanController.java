package com.mottinut.nutritionplan.presentation.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.auth.infrastructure.security.CustomUserPrincipal;
import com.mottinut.nutritionplan.domain.entities.NutritionPlan;

import com.mottinut.nutritionplan.domain.enums.PatientAction;
import com.mottinut.nutritionplan.domain.services.NutritionPlanService;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.nutritionplan.presentation.dto.request.PendingPatientAcceptanceDto;
import com.mottinut.nutritionplan.presentation.dto.response.DailyPlanResponseDto;
import com.mottinut.nutritionplan.presentation.dto.response.NutritionPlanResponseDto;
import com.mottinut.nutritionplan.presentation.dto.response.PatientPlanResponseDto;
import com.mottinut.nutritionplan.presentation.dto.response.WeeklyPlanResponseDto;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import java.util.Map;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/my-nutrition-plan")
@PreAuthorize("hasRole('PATIENT')")
@CrossOrigin(origins = "*")
public class MyNutritionPlanController {

    private final NutritionPlanService nutritionPlanService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public MyNutritionPlanController(NutritionPlanService nutritionPlanService, ObjectMapper objectMapper, UserService userService) {
        this.nutritionPlanService = nutritionPlanService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    // NUEVO: Endpoint para obtener planes pendientes de aceptación por el paciente
    @GetMapping("/pending-acceptance")
    public ResponseEntity<List<PendingPatientAcceptanceDto>> getPendingAcceptancePlans(Authentication authentication) {
        UserId patientId = getCurrentUserId(authentication);
        List<NutritionPlan> pendingPlans = nutritionPlanService.getPendingPatientAcceptancePlans(patientId);

        List<PendingPatientAcceptanceDto> response = pendingPlans.stream()
                .map(this::buildPendingAcceptanceResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // NUEVO: Endpoint para responder (aceptar/rechazar) a un plan
    @PostMapping("/respond/{planId}")
    public ResponseEntity<NutritionPlanResponseDto> respondToPlan(
            @PathVariable Long planId,
            @Valid @RequestBody PatientPlanResponseDto request,
            Authentication authentication) {

        UserId patientId = getCurrentUserId(authentication);
        NutritionPlanId nutritionPlanId = new NutritionPlanId(planId);
        PatientAction action = PatientAction.fromString(request.getAction());

        NutritionPlan respondedPlan = nutritionPlanService.patientRespondToPlan(
                patientId, nutritionPlanId, action, request.getFeedback());

        NutritionPlanResponseDto response = buildPlanResponse(respondedPlan);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    public ResponseEntity<DailyPlanResponseDto> getTodayPlan(Authentication authentication) {
        UserId patientId = getCurrentUserId(authentication);
        LocalDate today = LocalDate.now();

        Optional<NutritionPlan> planOpt = nutritionPlanService.getPatientActivePlan(patientId, today);

        if (planOpt.isEmpty()) {
            throw new NotFoundException("No tienes un plan nutricional aprobado para esta fecha");
        }

        int dayNumber = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        DailyPlanResponseDto dailyPlan = extractDailyPlan(planOpt.get(), dayNumber, today);

        return ResponseEntity.ok(dailyPlan);
    }

    @GetMapping("/day/{dayNumber}")
    public ResponseEntity<DailyPlanResponseDto> getDayPlan(
            @PathVariable Integer dayNumber,
            @RequestParam(required = false) String date,
            Authentication authentication) {

        if (dayNumber < 1 || dayNumber > 7) {
            throw new ValidationException("El número de día debe estar entre 1 (lunes) y 7 (domingo)");
        }

        UserId patientId = getCurrentUserId(authentication);
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        Optional<NutritionPlan> planOpt = nutritionPlanService.getPatientActivePlan(patientId, targetDate);

        if (planOpt.isEmpty()) {
            throw new NotFoundException("No tienes un plan nutricional aprobado para esta fecha");
        }

        // Calcular la fecha exacta del día solicitado basado en el plan
        LocalDate planStartDate = planOpt.get().getWeekStartDate();
        LocalDate specificDate = planStartDate.plusDays(dayNumber - 1);

        DailyPlanResponseDto dailyPlan = extractDailyPlan(planOpt.get(), dayNumber, specificDate);

        return ResponseEntity.ok(dailyPlan);
    }

    @GetMapping("/weekly")
    public ResponseEntity<WeeklyPlanResponseDto> getWeeklyPlan(
            @RequestParam(required = false) String date,
            Authentication authentication) {

        UserId patientId = getCurrentUserId(authentication);
        LocalDate referenceDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        Optional<NutritionPlan> planOpt = nutritionPlanService.getPatientActivePlan(patientId, referenceDate);

        if (planOpt.isEmpty()) {
            throw new NotFoundException("No tienes un plan nutricional aprobado para esta fecha");
        }

        WeeklyPlanResponseDto weeklyPlan = buildWeeklyPlanResponse(planOpt.get());
        return ResponseEntity.ok(weeklyPlan);
    }

    // NUEVO: Endpoint para obtener historial de planes aceptados
    @GetMapping("/history")
    public ResponseEntity<List<WeeklyPlanResponseDto>> getPlanHistory(Authentication authentication) {
        UserId patientId = getCurrentUserId(authentication);
        List<NutritionPlan> plans = nutritionPlanService.getPatientPlans(patientId);

        List<WeeklyPlanResponseDto> response = plans.stream()
                .map(this::buildWeeklyPlanResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }

    private DailyPlanResponseDto extractDailyPlan(NutritionPlan plan, int dayNumber, LocalDate date) {
        try {
            JsonNode planJson = objectMapper.readTree(plan.getPlanContent());
            JsonNode dailyPlans = planJson.get("daily_plans");

            if (dailyPlans != null && dailyPlans.isArray() && dailyPlans.size() >= dayNumber) {
                JsonNode dayPlan = dailyPlans.get(dayNumber - 1);

                return DailyPlanResponseDto.builder()
                        .date(date.toString())
                        .dayName(getDayName(dayNumber))
                        .meals(objectMapper.convertValue(dayPlan.get("meals"), new TypeReference<Map<String, Object>>() {}))
                        .totalCalories(dayPlan.get("total_calories").asInt())
                        .macronutrients(objectMapper.convertValue(dayPlan.get("macronutrients"), new TypeReference<Map<String, Number>>() {}))
                        .build();
            }

            throw new NotFoundException("Plan del día no encontrado");

        } catch (Exception e) {
            throw new RuntimeException("Error procesando el plan nutricional: " + e.getMessage());
        }
    }

    private WeeklyPlanResponseDto buildWeeklyPlanResponse(NutritionPlan plan) {
        try {
            JsonNode planJson = objectMapper.readTree(plan.getPlanContent());
            JsonNode dailyPlans = planJson.get("daily_plans");

            List<DailyPlanResponseDto> dailyPlanList = new ArrayList<>();

            if (dailyPlans != null && dailyPlans.isArray()) {
                for (int i = 0; i < Math.min(7, dailyPlans.size()); i++) {
                    JsonNode dayPlan = dailyPlans.get(i);
                    LocalDate dayDate = plan.getWeekStartDate().plusDays(i);

                    DailyPlanResponseDto dailyPlan = DailyPlanResponseDto.builder()
                            .date(dayDate.toString())
                            .dayName(getDayName(i + 1))
                            .meals(objectMapper.convertValue(dayPlan.get("meals"), Map.class))
                            .totalCalories(dayPlan.get("total_calories").asInt())
                            .macronutrients(objectMapper.convertValue(dayPlan.get("macronutrients"), Map.class))
                            .build();

                    dailyPlanList.add(dailyPlan);
                }
            }

            LocalDate weekEnd = plan.getWeekStartDate().plusDays(6);

            return WeeklyPlanResponseDto.builder()
                    .planId(plan.getPlanId().getValue())
                    .weekStartDate(plan.getWeekStartDate().toString())
                    .weekEndDate(weekEnd.toString())
                    .goal(plan.getGoal())
                    .energyRequirement(plan.getEnergyRequirement())
                    .dailyPlans(dailyPlanList)
                    .reviewNotes(plan.getReviewNotes())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error procesando el plan semanal: " + e.getMessage());
        }
    }

    private PendingPatientAcceptanceDto buildPendingAcceptanceResponse(NutritionPlan plan) {
        User nutritionist = userService.getUserById(plan.getNutritionistId());

        return PendingPatientAcceptanceDto.builder()
                .planId(plan.getPlanId().getValue())
                .nutritionistName(nutritionist.getFullName())
                .weekStartDate(plan.getWeekStartDate().toString())
                .energyRequirement(plan.getEnergyRequirement())
                .goal(plan.getGoal())
                .specialRequirements(plan.getSpecialRequirements())
                .reviewNotes(plan.getReviewNotes())
                .reviewedAt(plan.getReviewedAt() != null ? plan.getReviewedAt().toString() : null)
                .build();
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

    private String getDayName(int dayNumber) {
        String[] days = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        return days[dayNumber - 1];
    }
}