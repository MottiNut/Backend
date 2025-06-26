package com.mottinut.nutritionplan.domain.valueobjects;

import com.mottinut.shared.domain.exceptions.ValidationException;
import lombok.Getter;

@Getter
public class NutritionPlanId {
    private final Long value;

    public NutritionPlanId(Long value) {
        if (value == null || value <= 0) {
            throw new ValidationException("ID del plan nutricional debe ser válido");
        }
        this.value = value;
    }
}