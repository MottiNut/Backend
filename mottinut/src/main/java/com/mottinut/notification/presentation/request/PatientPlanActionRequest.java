package com.mottinut.notification.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PatientPlanActionRequest {
    @NotBlank
    private Long patientId;

    @NotBlank
    private Long nutritionistId;

    @NotNull
    private Long planId;

    @NotBlank
    private String patientName;

    @NotBlank
    private String actionType;

    private String reason;
}