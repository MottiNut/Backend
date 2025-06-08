package com.mottinut.shared.domain.valueobjects;

import jakarta.validation.ValidationException;

import java.util.concurrent.ThreadLocalRandom;

public class UserId {
    private final Long value;

    public UserId(Long value) {
        if (value == null || value <= 0) {
            throw new ValidationException("El ID de usuario debe ser un número positivo");
        }
        this.value = value;
    }

    public static UserId generate() {
        // En un entorno real, esto sería manejado por la base de datos
        // Este método es solo para casos donde necesitemos generar un ID temporal
        return new UserId(ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE));
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserId userId = (UserId) obj;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

