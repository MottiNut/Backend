package com.mottinut.bff.nutritionplan.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratePlanRequestDto {
    @NotNull(message = "ID del paciente es requerido")
    private Long patientUserId;

    @NotNull(message = "Fecha de inicio de semana es requerida")
    private String weekStartDate; // yyyy-MM-dd

    @NotNull(message = "Requerimiento energético es requerido")
    @Min(value = 1000, message = "Requerimiento energético debe ser al menos 1000 kcal")
    @Max(value = 5000, message = "Requerimiento energético no debe exceder 5000 kcal")
    private Integer energyRequirement;

    @NotBlank(message = "Objetivo es requerido")
    private String goal;

    private String specialRequirements;

    private Integer mealsPerDay;
}
