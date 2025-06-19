package com.mottinut.nutritionplan.domain.services;

import com.mottinut.shared.domain.valueobjects.UserId;

import java.time.LocalDate;

public interface AiPlanGeneratorService {
    String generatePlan(UserId patientId, LocalDate weekStartDate, Integer energyRequirement,
                        String goal, String specialRequirements, Integer mealsPerDay);
}
