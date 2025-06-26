package com.mottinut.auth.domain.valueobjects;

import jakarta.validation.ValidationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class LicenseNumber {
    private final String value;

    public LicenseNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("El número de colegiatura es obligatorio.");
        }
        if (value.length() != 4) {
            throw new ValidationException("El número de colegiatura debe tener exactamente 4 caracteres.");
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
