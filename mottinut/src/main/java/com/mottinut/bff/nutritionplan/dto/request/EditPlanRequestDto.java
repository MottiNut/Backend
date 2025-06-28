package com.mottinut.bff.nutritionplan.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditPlanRequestDto {
    @NotNull(message = "El contenido del plan es requerido")
    @Valid
    private Object planContent;

    private String reviewNotes;

    // Método helper para validar el contenido
    @AssertTrue(message = "El contenido del plan debe ser un objeto JSON válido")
    public boolean isPlanContentValid() {
        if (planContent == null) {
            return false;
        }

        // Si es String, debe ser JSON válido
        if (planContent instanceof String) {
            String content = (String) planContent;
            return content.trim().startsWith("{") && content.trim().endsWith("}");
        }

        // Si es Map o cualquier otro objeto, es válido
        return true;
    }
}
