package com.mottinut.bff.nutritionplan.controller;


import com.mottinut.bff.nutritionplan.dto.request.PendingPatientAcceptanceDto;
import com.mottinut.bff.nutritionplan.dto.response.DailyPlanResponseDto;
import com.mottinut.bff.nutritionplan.dto.response.NutritionPlanResponseDto;
import com.mottinut.bff.nutritionplan.dto.response.PatientPlanResponseDto;
import com.mottinut.bff.nutritionplan.dto.response.WeeklyPlanResponseDto;
import com.mottinut.bff.nutritionplan.service.PatientNutritionPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff/patient/nutrition-plans")
@PreAuthorize("hasRole('PATIENT')")
@CrossOrigin(origins = "*")
public class PatientNutritionPlanController {

    private final PatientNutritionPlanService patientService;

    public PatientNutritionPlanController(PatientNutritionPlanService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/pending-acceptance")
    public ResponseEntity<List<PendingPatientAcceptanceDto>> getPendingAcceptancePlans(Authentication authentication) {
        List<PendingPatientAcceptanceDto> response = patientService.getPendingAcceptancePlans(authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{planId}/respond")
    public ResponseEntity<NutritionPlanResponseDto> respondToPlan(
            @PathVariable Long planId,
            @Valid @RequestBody PatientPlanResponseDto request,
            Authentication authentication) {

        NutritionPlanResponseDto response = patientService.respondToPlan(planId, request, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today")
    public ResponseEntity<DailyPlanResponseDto> getTodayPlan(Authentication authentication) {
        DailyPlanResponseDto response = patientService.getTodayPlan(authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/day/{dayNumber}")
    public ResponseEntity<DailyPlanResponseDto> getDayPlan(
            @PathVariable Integer dayNumber,
            @RequestParam(required = false) String date,
            Authentication authentication) {

        DailyPlanResponseDto response = patientService.getDayPlan(dayNumber, date, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/weekly")
    public ResponseEntity<WeeklyPlanResponseDto> getWeeklyPlan(
            @RequestParam(required = false) String date,
            Authentication authentication) {

        WeeklyPlanResponseDto response = patientService.getWeeklyPlan(date, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<WeeklyPlanResponseDto>> getPlanHistory(Authentication authentication) {
        List<WeeklyPlanResponseDto> response = patientService.getPlanHistory(authentication);
        return ResponseEntity.ok(response);
    }
}
