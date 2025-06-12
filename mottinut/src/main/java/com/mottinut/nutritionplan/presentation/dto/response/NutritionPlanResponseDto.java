package com.mottinut.nutritionplan.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NutritionPlanResponseDto {
    private Long planId;
    private Long patientId;
    private String patientName;
    private Long nutritionistId;
    private String nutritionistName;
    private String weekStartDate;
    private Integer energyRequirement;
    private String goal;
    private String specialRequirements;
    private String planContent;
    private String status;
    private String reviewNotes;
    private String createdAt;
    private String reviewedAt;
}
