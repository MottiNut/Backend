package com.mottinut.bff.nutritionplan.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectedByPatientDto {
    private Long planId;
    private Long patientId;
    private String patientName;
    private String weekStartDate;
    private Integer energyRequirement;
    private String goal;
    private String patientFeedback;
    private String patientResponseAt;
}