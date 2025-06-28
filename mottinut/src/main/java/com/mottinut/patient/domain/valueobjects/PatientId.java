package com.mottinut.patient.domain.valueobjects;

import com.mottinut.shared.domain.exceptions.ValidationException;
import lombok.Value;

@Value
public class PatientId {
    Long value;

    public static PatientId of(Long value) {
        if (value == null || value <= 0) {
            throw new ValidationException("ID de paciente inválido");
        }
        return new PatientId(value);
    }
}
