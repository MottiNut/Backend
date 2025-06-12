package com.mottinut.nutritionplan.domain.valueobjects;

import com.mottinut.shared.domain.exceptions.ValidationException;
import lombok.Getter;

@Getter
public class GenerationId {
    private final Long value;

    public GenerationId(Long value) {
        if (value == null || value <= 0) {
            throw new ValidationException("ID de generación debe ser válido");
        }
        this.value = value;
    }
}
