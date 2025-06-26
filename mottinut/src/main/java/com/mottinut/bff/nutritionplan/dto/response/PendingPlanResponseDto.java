package com.mottinut.bff.nutritionplan.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PendingPlanResponseDto {
    private Long planId;
    private Long patientId;
    private String patientName;
    private String weekStartDate;
    private Integer energyRequirement;
    private String goal;
    private String specialRequirements;
    private String createdAt;
}
