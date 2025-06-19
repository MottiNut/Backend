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
    List<NutritionPlan> findPendingPatientAcceptancePlans(UserId patientId);
    Optional<NutritionPlan> findAcceptedPlanByPatientAndWeekRange(UserId patientId, LocalDate startDate, LocalDate endDate);
    List<NutritionPlan> findAcceptedPlansByPatient(UserId patientId);
    List<NutritionPlan> findRejectedByPatientPlans(UserId nutritionistId);
    List<NutritionPlan> findPendingPlansByNutritionist(UserId nutritionistId);
}