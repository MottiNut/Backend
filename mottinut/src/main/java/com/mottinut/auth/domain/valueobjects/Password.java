package com.mottinut.auth.domain.valueobjects;

import jakarta.validation.ValidationException;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
public class Password {
    private final String hashedValue;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    public static Password fromPlainText(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            throw new ValidationException("La contraseña no puede estar vacía");
        }

        if (plainText.length() < 6) {
            throw new ValidationException("La contraseña debe tener al menos 6 caracteres");
        }

        String hashed = encoder.encode(plainText);
        return new Password(hashed);
    }

    public static Password fromHash(String hashedValue) {
        if (hashedValue == null || hashedValue.trim().isEmpty()) {
            throw new ValidationException("El hash de contraseña no puede estar vacío");
        }
        return new Password(hashedValue);
    }

    public boolean matches(String plainText) {
        return encoder.matches(plainText, hashedValue);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Password password = (Password) obj;
        return hashedValue.equals(password.hashedValue);
    }

    @Override
    public int hashCode() {
        return hashedValue.hashCode();
    }
}
