package com.mottinut.nutritionplan.infrastructure.persistence.mappers;

import com.mottinut.nutritionplan.domain.entities.NutritionPlan;
import com.mottinut.nutritionplan.domain.enums.PlanStatus;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.nutritionplan.infrastructure.persistence.entities.NutritionPlanEntity;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class NutritionPlanMapper {

    public NutritionPlanEntity toEntity(NutritionPlan plan) {
        NutritionPlanEntity entity = new NutritionPlanEntity();
        if (plan.getPlanId() != null) {
            entity.setPlanId(plan.getPlanId().getValue());
        }
        entity.setPatientId(plan.getPatientId().getValue());
        entity.setNutritionistId(plan.getNutritionistId().getValue());
        entity.setWeekStartDate(plan.getWeekStartDate());
        entity.setEnergyRequirement(plan.getEnergyRequirement());
        entity.setGoal(plan.getGoal());
        entity.setSpecialRequirements(plan.getSpecialRequirements());
        entity.setMealsPerDay(plan.getMealsPerDay());
        entity.setPlanContent(plan.getPlanContent());
        entity.setStatus(plan.getStatus().getValue());
        entity.setReviewNotes(plan.getReviewNotes());
        entity.setPatientFeedback(plan.getPatientFeedback());
        entity.setCreatedAt(plan.getCreatedAt());
        entity.setReviewedAt(plan.getReviewedAt());
        entity.setPatientResponseAt(plan.getPatientResponseAt());
        return entity;
    }

    public NutritionPlan toDomain(NutritionPlanEntity entity) {
        NutritionPlan plan = new NutritionPlan(
                new NutritionPlanId(entity.getPlanId()),
                new UserId(entity.getPatientId()),
                new UserId(entity.getNutritionistId()),
                entity.getWeekStartDate(),
                entity.getEnergyRequirement(),
                entity.getGoal(),
                entity.getSpecialRequirements(),
                entity.getMealsPerDay(),
                entity.getPlanContent()
        );

        try {
            Field statusField = NutritionPlan.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(plan, PlanStatus.fromString(entity.getStatus()));

            Field reviewNotesField = NutritionPlan.class.getDeclaredField("reviewNotes");
            reviewNotesField.setAccessible(true);
            reviewNotesField.set(plan, entity.getReviewNotes());

            Field patientFeedbackField = NutritionPlan.class.getDeclaredField("patientFeedback");
            patientFeedbackField.setAccessible(true);
            patientFeedbackField.set(plan, entity.getPatientFeedback());

            Field reviewedAtField = NutritionPlan.class.getDeclaredField("reviewedAt");
            reviewedAtField.setAccessible(true);
            reviewedAtField.set(plan, entity.getReviewedAt());

            Field patientResponseAtField = NutritionPlan.class.getDeclaredField("patientResponseAt");
            patientResponseAtField.setAccessible(true);
            patientResponseAtField.set(plan, entity.getPatientResponseAt());

        } catch (Exception e) {
            throw new RuntimeException("Error mapping entity to domain", e);
        }

        return plan;
    }
}
