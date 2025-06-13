package com.mottinut.bff.nutritionplan.dto.response;

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
    private Object planContent; // Cambio de String a Object
    private String status;
    private String reviewNotes;
    private String createdAt;
    private String reviewedAt;
}