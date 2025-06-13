package com.mottinut.bff.nutritionplan.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewPlanRequestDto {
    @NotBlank(message = "Acción es requerida")
    private String action; // "approve" or "reject"

    @NotBlank(message = "Notas de revisión son requeridas")
    private String reviewNotes;
}
