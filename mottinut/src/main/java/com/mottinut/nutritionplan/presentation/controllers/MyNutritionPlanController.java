package com.mottinut.nutritionplan.presentation.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mottinut.auth.infrastructure.security.CustomUserPrincipal;
import com.mottinut.nutritionplan.domain.entities.NutritionPlan;

import com.mottinut.nutritionplan.domain.services.NutritionPlanService;
import com.mottinut.nutritionplan.presentation.dto.response.DailyPlanResponseDto;
import com.mottinut.nutritionplan.presentation.dto.response.WeeklyPlanResponseDto;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/my-nutrition-plan")
@PreAuthorize("hasRole('PATIENT')")
@CrossOrigin(origins = "*")
public class MyNutritionPlanController {

    private final NutritionPlanService nutritionPlanService;
    private final ObjectMapper objectMapper;

    public MyNutritionPlanController(NutritionPlanService nutritionPlanService, ObjectMapper objectMapper) {
        this.nutritionPlanService = nutritionPlanService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/today")
    public ResponseEntity<DailyPlanResponseDto> getTodayPlan(Authentication authentication) {
        UserId patientId = getCurrentUserId(authentication);
        LocalDate today = LocalDate.now();
        LocalDate weekStart = getWeekStart(today);

        Optional<NutritionPlan> planOpt = nutritionPlanService.getPatientPlanForWeek(patientId, weekStart);

        if (planOpt.isEmpty()) {
            throw new NotFoundException("No tienes un plan nutricional aprobado para esta semana");
        }

        int dayNumber = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
        DailyPlanResponseDto dailyPlan = extractDailyPlan(planOpt.get(), dayNumber, today);

        return ResponseEntity.ok(dailyPlan);
    }

    @GetMapping("/day/{dayNumber}")
    public ResponseEntity<DailyPlanResponseDto> getDayPlan(
            @PathVariable Integer dayNumber,
            @RequestParam(required = false) String weekStart,
            Authentication authentication) {

        if (dayNumber < 1 || dayNumber > 7) {
            throw new ValidationException("El número de día debe estar entre 1 (lunes) y 7 (domingo)");
        }

        UserId patientId = getCurrentUserId(authentication);
        LocalDate weekStartDate = weekStart != null ?
                LocalDate.parse(weekStart) : getWeekStart(LocalDate.now());

        Optional<NutritionPlan> planOpt = nutritionPlanService.getPatientPlanForWeek(patientId, weekStartDate);

        if (planOpt.isEmpty()) {
            throw new NotFoundException("No tienes un plan nutricional aprobado para esta semana");
        }

        LocalDate targetDate = weekStartDate.plusDays(dayNumber - 1);
        DailyPlanResponseDto dailyPlan = extractDailyPlan(planOpt.get(), dayNumber, targetDate);

        return ResponseEntity.ok(dailyPlan);
    }

    @GetMapping("/weekly")
    public ResponseEntity<WeeklyPlanResponseDto> getWeeklyPlan(
            @RequestParam(required = false) String weekStart,
            Authentication authentication) {

        UserId patientId = getCurrentUserId(authentication);
        LocalDate weekStartDate = weekStart != null ?
                LocalDate.parse(weekStart) : getWeekStart(LocalDate.now());

        Optional<NutritionPlan> planOpt = nutritionPlanService.getPatientPlanForWeek(patientId, weekStartDate);

        if (planOpt.isEmpty()) {
            throw new NotFoundException("No tienes un plan nutricional aprobado para esta semana");
        }

        WeeklyPlanResponseDto weeklyPlan = buildWeeklyPlanResponse(planOpt.get());
        return ResponseEntity.ok(weeklyPlan);
    }

    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }

    private LocalDate getWeekStart(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
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
                        .macronutrients(objectMapper.convertValue(dayPlan.get("macronutrients"), new TypeReference<Map<String, Double>>() {}))
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

    private String getDayName(int dayNumber) {
        String[] days = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        return days[dayNumber - 1];
    }
}