package com.mottinut.bff.nutritionplan.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetailedNutritionPlanDto {
    private Long planId;
    private Long patientId;
    private String patientName;
    private Long nutritionistId;
    private String nutritionistName;
    private String weekStartDate;
    private Integer energyRequirement;
    private String goal;
    private String specialRequirements;
    private Object planContent; // Será parseado como JSON
    private String status;
    private String reviewNotes;
    private String patientFeedback;
    private String createdAt;
    private String reviewedAt;
    private String patientResponseAt;
}
