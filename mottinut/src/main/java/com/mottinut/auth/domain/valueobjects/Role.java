package com.mottinut.auth.domain.valueobjects;

import jakarta.validation.ValidationException;
import lombok.Getter;

@Getter
public enum Role {
    PATIENT("patient"),
    NUTRITIONIST("nutritionist");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public static Role fromString(String value) {
        for (Role role : Role.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new ValidationException("Rol inválido: " + value);
    }

    public boolean isPatient() {
        return this == PATIENT;
    }

    public boolean isNutritionist() {
        return this == NUTRITIONIST;
    }

    @Override
    public String toString() {
        return value;
    }
}
