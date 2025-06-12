package com.mottinut.nutritionplan.domain.entities;

import com.mottinut.nutritionplan.domain.enums.PlanStatus;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class NutritionPlan {
    private final NutritionPlanId planId;
    private final UserId patientId;
    private final UserId nutritionistId;
    private final LocalDate weekStartDate;
    private final Integer energyRequirement;
    private final String goal;
    private final String specialRequirements;
    private final String planContent; // JSON con el plan completo
    private PlanStatus status;
    private String reviewNotes;
    private final LocalDateTime createdAt;
    private LocalDateTime reviewedAt;

    public NutritionPlan(NutritionPlanId planId, UserId patientId, UserId nutritionistId,
                         LocalDate weekStartDate, Integer energyRequirement, String goal,
                         String specialRequirements, String planContent) {
        this.planId = planId;
        this.patientId = patientId;
        this.nutritionistId = nutritionistId;
        this.weekStartDate = weekStartDate;
        this.energyRequirement = energyRequirement;
        this.goal = goal;
        this.specialRequirements = specialRequirements;
        this.planContent = planContent;
        this.status = PlanStatus.PENDING_REVIEW;
        this.createdAt = LocalDateTime.now();
    }

    public void approve(String reviewNotes) {
        this.status = PlanStatus.APPROVED;
        this.reviewNotes = reviewNotes;
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject(String reviewNotes) {
        this.status = PlanStatus.REJECTED;
        this.reviewNotes = reviewNotes;
        this.reviewedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return status == PlanStatus.PENDING_REVIEW;
    }

    public boolean isApproved() {
        return status == PlanStatus.APPROVED;
    }
}