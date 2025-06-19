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
    private final Integer mealsPerDay;
    private String planContent;
    private PlanStatus status;
    private String reviewNotes;
    private String patientFeedback; // Nuevo campo
    private final LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime patientResponseAt; // Nuevo campo

    public NutritionPlan(NutritionPlanId planId, UserId patientId, UserId nutritionistId,
                         LocalDate weekStartDate, Integer energyRequirement, String goal,
                         String specialRequirements, Integer mealsPerDay, String planContent) {
        this.planId = planId;
        this.patientId = patientId;
        this.nutritionistId = nutritionistId;
        this.weekStartDate = weekStartDate;
        this.energyRequirement = energyRequirement;
        this.goal = goal;
        this.specialRequirements = specialRequirements;
        this.mealsPerDay = mealsPerDay;
        this.planContent = planContent;
        this.status = PlanStatus.PENDING_REVIEW;
        this.createdAt = LocalDateTime.now();
    }

    public void approve(String reviewNotes) {
        this.status = PlanStatus.PENDING_PATIENT_ACCEPTANCE;
        this.reviewNotes = reviewNotes;
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject(String reviewNotes) {
        this.status = PlanStatus.REJECTED;
        this.reviewNotes = reviewNotes;
        this.reviewedAt = LocalDateTime.now();
    }

    public void editPlan(String newPlanContent, String reviewNotes) {
        this.planContent = newPlanContent;
        this.status = PlanStatus.PENDING_PATIENT_ACCEPTANCE;
        this.reviewNotes = reviewNotes;
        this.reviewedAt = LocalDateTime.now();
    }

    public void acceptByPatient(String patientFeedback) {
        this.status = PlanStatus.ACCEPTED_BY_PATIENT;
        this.patientFeedback = patientFeedback;
        this.patientResponseAt = LocalDateTime.now();
    }

    public void rejectByPatient(String patientFeedback) {
        this.status = PlanStatus.REJECTED_BY_PATIENT;
        this.patientFeedback = patientFeedback;
        this.patientResponseAt = LocalDateTime.now();
    }

    // Métodos de estado
    public boolean isPending() {
        return status == PlanStatus.PENDING_REVIEW;
    }

    public boolean isPendingPatientAcceptance() {
        return status == PlanStatus.PENDING_PATIENT_ACCEPTANCE;
    }

    public boolean isApproved() {
        return status == PlanStatus.APPROVED;
    }

    public boolean isAcceptedByPatient() {
        return status == PlanStatus.ACCEPTED_BY_PATIENT;
    }

    public boolean isRejectedByPatient() {
        return status == PlanStatus.REJECTED_BY_PATIENT;
    }

    public boolean canBeEditedByNutritionist() {
        return status == PlanStatus.REJECTED || status == PlanStatus.REJECTED_BY_PATIENT;
    }
}