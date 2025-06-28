package com.mottinut.bff.nutritionplan.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingPatientAcceptanceDto {
    private Long planId;
    private String nutritionistName;
    private String weekStartDate;
    private Integer energyRequirement;
    private String goal;
    private String specialRequirements;
    private String reviewNotes;
    private String reviewedAt;
}
