package com.mottinut.auth.domain.valueobjects;

import jakarta.validation.ValidationException;
import lombok.Getter;

@Getter
public class Token {
    private final String value;

    private Token(String value) {
        this.value = value;
    }

    public static Token create(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("El token no puede estar vacío");
        }
        return new Token(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Token token = (Token) obj;
        return value.equals(token.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
