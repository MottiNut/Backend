package com.mottinut.patient.domain.valueobjects;

import com.mottinut.shared.domain.exceptions.ValidationException;
import lombok.Value;

@Value
public class MedicalHistoryId {
    Long value;

    public static MedicalHistoryId of(Long value) {
        if (value == null || value <= 0) {
            throw new ValidationException("ID de historial médico inválido");
        }
        return new MedicalHistoryId(value);
    }
}
