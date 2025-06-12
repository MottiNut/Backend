package com.mottinut.nutritionplan.domain.repositories;

import com.mottinut.nutritionplan.domain.entities.NutritionPlan;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.shared.domain.valueobjects.UserId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NutritionPlanRepository {
    NutritionPlan save(NutritionPlan plan);
    Optional<NutritionPlan> findById(NutritionPlanId planId);
    List<NutritionPlan> findPendingPlans();
    Optional<NutritionPlan> findApprovedPlanByPatientAndWeek(UserId patientId, LocalDate weekStartDate);
    List<NutritionPlan> findApprovedPlansByPatient(UserId patientId);
}