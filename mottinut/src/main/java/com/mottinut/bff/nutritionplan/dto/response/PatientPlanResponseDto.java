package com.mottinut.bff.nutritionplan.dto.response;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatientPlanResponseDto {
    @NotNull(message = "La acción es requerida")
    @Pattern(regexp = "accept|reject", message = "La acción debe ser 'accept' o 'reject'")
    private String action;

    private String feedback;
}