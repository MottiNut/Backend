package com.mottinut.bff.nutritionplan.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.bff.nutritionplan.dto.request.PendingPatientAcceptanceDto;
import com.mottinut.bff.nutritionplan.dto.response.DailyPlanResponseDto;
import com.mottinut.bff.nutritionplan.dto.response.NutritionPlanResponseDto;
import com.mottinut.bff.nutritionplan.dto.response.PatientPlanResponseDto;
import com.mottinut.bff.nutritionplan.dto.response.WeeklyPlanResponseDto;
import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.nutritionplan.domain.entities.NutritionPlan;
import com.mottinut.nutritionplan.domain.enums.PatientAction;
import com.mottinut.nutritionplan.domain.services.NutritionPlanService;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientNutritionPlanService {

    private final NutritionPlanService nutritionPlanService;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(PatientNutritionPlanService.class);

    public PatientNutritionPlanService(NutritionPlanService nutritionPlanService,
                                       ObjectMapper objectMapper,
                                       UserService userService) {
        this.nutritionPlanService = nutritionPlanService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    public List<PendingPatientAcceptanceDto> getPendingAcceptancePlans(Authentication authentication) {
        UserId patientId = getCurrentUserId(authentication);
        List<NutritionPlan> pendingPlans = nutritionPlanService.getPendingPatientAcceptancePlans(patientId);

        return pendingPlans.stream()
                .map(this::buildPendingAcceptanceResponse)
                .collect(Collectors.toList());
    }

    public NutritionPlanResponseDto respondToPlan(Long planId, PatientPlanResponseDto request, Authentication authentication) {
        try {
            UserId patientId = getCurrentUserId(authentication);
            NutritionPlanId nutritionPlanId = new NutritionPlanId(planId);
            PatientAction action = PatientAction.fromString(request.getAction());

            NutritionPlan respondedPlan = nutritionPlanService.patientRespondToPlan(
                    patientId, nutritionPlanId, action, request.getFeedback());

            return buildPlanResponse(respondedPlan);
        } catch (Exception e) {
            throw new RuntimeException("Error respondiendo al plan: " + e.getMessage(), e);
        }
    }

    public DailyPlanResponseDto getTodayPlan(Authentication authentication) {
        UserId patientId = getCurrentUserId(authentication);
        LocalDate today = LocalDate.now();

        Optional<NutritionPlan> planOpt = nutritionPlanService.getPatientActivePlan(patientId, today);

        if (planOpt.isEmpty()) {
            throw new NotFoundException("No tienes un plan nutricional aprobado para esta fecha");
        }

        int dayNumber = today.getDayOfWeek().getValue();
        return extractDailyPlan(planOpt.get(), dayNumber, today);
    }

    public DailyPlanResponseDto getDayPlan(Integer dayNumber, String date, Authentication authentication) {
        if (dayNumber < 1 || dayNumber > 7) {
            throw new ValidationException("El número de día debe estar entre 1 (lunes) y 7 (domingo)");
        }

        UserId patientId = getCurrentUserId(authentication);
        LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        Optional<NutritionPlan> planOpt = nutritionPlanService.getPatientActivePlan(patientId, targetDate);

        if (planOpt.isEmpty()) {
            throw new NotFoundException("No tienes un plan nutricional aprobado para esta fecha");
        }

        LocalDate planStartDate = planOpt.get().getWeekStartDate();
        LocalDate specificDate = planStartDate.plusDays(dayNumber - 1);

        return extractDailyPlan(planOpt.get(), dayNumber, specificDate);
    }

    public WeeklyPlanResponseDto getWeeklyPlan(String date, Authentication authentication) {
        UserId patientId = getCurrentUserId(authentication);
        LocalDate referenceDate = date != null ? LocalDate.parse(date) : LocalDate.now();

        Optional<NutritionPlan> planOpt = nutritionPlanService.getPatientActivePlan(patientId, referenceDate);

        if (planOpt.isEmpty()) {
            throw new NotFoundException("No tienes un plan nutricional aprobado para esta fecha");
        }

        return buildWeeklyPlanResponse(planOpt.get());
    }

    public List<WeeklyPlanResponseDto> getPlanHistory(Authentication authentication) {
        UserId patientId = getCurrentUserId(authentication);
        List<NutritionPlan> plans = nutritionPlanService.getPatientPlans(patientId);

        return plans.stream()
                .map(this::buildWeeklyPlanResponse)
                .collect(Collectors.toList());
    }

    // Helper methods
    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }

    private DailyPlanResponseDto extractDailyPlan(NutritionPlan plan, int dayNumber, LocalDate date) {
        try {
            JsonNode planJson = objectMapper.readTree(plan.getPlanContent());

            // Intentar ambos formatos: "days" (nuevo) y "daily_plans" (anterior)
            JsonNode dailyPlansNode = planJson.get("days");
            if (dailyPlansNode == null) {
                dailyPlansNode = planJson.get("daily_plans");
            }

            if (dailyPlansNode != null && dailyPlansNode.isArray() && dailyPlansNode.size() >= dayNumber) {
                JsonNode dayPlan = dailyPlansNode.get(dayNumber - 1);

                // Extraer las comidas del día
                Object meals; // Cambiar de Map<String, Object> a Object

                JsonNode mealsNode = dayPlan.get("meals");
                if (mealsNode != null && mealsNode.isArray()) {
                    // Formato: "meals": [{"type": "Desayuno", ...}, ...]
                    // Convertir directamente a lista sin envolver en otro objeto
                    meals = objectMapper.convertValue(mealsNode, List.class);
                } else if (mealsNode != null) {
                    // Formato alternativo: objeto con propiedades
                    meals = objectMapper.convertValue(mealsNode, new TypeReference<Map<String, Object>>() {});
                } else {
                    // Si no hay comidas, inicializar como lista vacía
                    meals = new ArrayList<>();
                }

                // Extraer calorías totales
                int totalCalories = plan.getEnergyRequirement(); // valor por defecto
                JsonNode caloriesNode = dayPlan.get("total_calories");
                if (caloriesNode != null) {
                    totalCalories = caloriesNode.asInt();
                }

                // Extraer macronutrientes
                Map<String, Number> macronutrients = new HashMap<>();
                JsonNode macrosNode = dayPlan.get("macronutrients");
                if (macrosNode != null) {
                    macronutrients = objectMapper.convertValue(macrosNode, new TypeReference<Map<String, Number>>() {});
                } else {
                    // Valores por defecto
                    macronutrients.put("protein", totalCalories * 0.20 / 4);
                    macronutrients.put("carbs", totalCalories * 0.50 / 4);
                    macronutrients.put("fat", totalCalories * 0.30 / 9);
                }

                return DailyPlanResponseDto.builder()
                        .date(date.toString())
                        .dayName(getDayName(dayNumber))
                        .meals(meals) // Ahora meals será directamente la lista o el objeto
                        .totalCalories(totalCalories)
                        .macronutrients(macronutrients)
                        .build();
            }

            throw new NotFoundException("Plan del día no encontrado");

        } catch (Exception e) {
            logger.error("Error procesando el plan del día {} para plan ID {}: {}",
                    dayNumber, plan.getPlanId().getValue(), e.getMessage(), e);
            throw new RuntimeException("Error procesando el plan nutricional: " + e.getMessage());
        }
    }

    private WeeklyPlanResponseDto buildWeeklyPlanResponse(NutritionPlan plan) {
        try {
            JsonNode planJson = objectMapper.readTree(plan.getPlanContent());

            // Intentar ambos formatos: "days" (nuevo) y "daily_plans" (anterior)
            JsonNode dailyPlansNode = planJson.get("days");
            if (dailyPlansNode == null) {
                dailyPlansNode = planJson.get("daily_plans");
            }

            List<DailyPlanResponseDto> dailyPlanList = new ArrayList<>();

            if (dailyPlansNode != null && dailyPlansNode.isArray()) {
                for (int i = 0; i < Math.min(7, dailyPlansNode.size()); i++) {
                    JsonNode dayPlan = dailyPlansNode.get(i);
                    LocalDate dayDate = plan.getWeekStartDate().plusDays(i);

                    // Extraer las comidas del día - CORRECCIÓN APLICADA
                    Object meals; // Cambiar de Map<String, Object> a Object

                    JsonNode mealsNode = dayPlan.get("meals");
                    if (mealsNode != null && mealsNode.isArray()) {
                        // Formato: "meals": [{"type": "Desayuno", ...}, ...]
                        // Convertir directamente a lista sin envolver en otro objeto
                        meals = objectMapper.convertValue(mealsNode, List.class);
                    } else if (mealsNode != null) {
                        // Formato alternativo: objeto con propiedades
                        meals = objectMapper.convertValue(mealsNode, new TypeReference<Map<String, Object>>() {});
                    } else {
                        // Si no hay comidas, inicializar como lista vacía
                        meals = new ArrayList<>();
                    }

                    // Extraer calorías totales
                    int totalCalories = 0;
                    JsonNode caloriesNode = dayPlan.get("total_calories");
                    if (caloriesNode != null) {
                        totalCalories = caloriesNode.asInt();
                    } else {
                        // Calcular calorías si no están especificadas
                        totalCalories = plan.getEnergyRequirement();
                    }

                    // Extraer macronutrientes
                    Map<String, Number> macronutrients = new HashMap<>();
                    JsonNode macrosNode = dayPlan.get("macronutrients");
                    if (macrosNode != null) {
                        macronutrients = objectMapper.convertValue(macrosNode, Map.class);
                    } else {
                        // Valores por defecto si no están especificados
                        macronutrients.put("protein", totalCalories * 0.20 / 4); // 20% proteínas
                        macronutrients.put("carbs", totalCalories * 0.50 / 4);   // 50% carbohidratos
                        macronutrients.put("fat", totalCalories * 0.30 / 9);     // 30% grasas
                    }

                    DailyPlanResponseDto dailyPlan = DailyPlanResponseDto.builder()
                            .date(dayDate.toString())
                            .dayName(getDayName(i + 1))
                            .meals(meals) // Ahora meals será directamente la lista o el objeto
                            .totalCalories(totalCalories)
                            .macronutrients(macronutrients)
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
            logger.error("Error procesando el plan semanal para plan ID {}: {}",
                    plan.getPlanId().getValue(), e.getMessage(), e);

            // En caso de error, devolver respuesta con información básica
            return WeeklyPlanResponseDto.builder()
                    .planId(plan.getPlanId().getValue())
                    .weekStartDate(plan.getWeekStartDate().toString())
                    .weekEndDate(plan.getWeekStartDate().plusDays(6).toString())
                    .goal(plan.getGoal())
                    .energyRequirement(plan.getEnergyRequirement())
                    .dailyPlans(new ArrayList<>())
                    .reviewNotes(plan.getReviewNotes())
                    .build();
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
