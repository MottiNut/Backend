package com.mottinut.auth.domain.valueobjects;

import jakarta.validation.ValidationException;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.regex.Pattern;

@Getter
public class Password {
    private final String hashedValue;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Patrón para al menos 1 minúscula, 1 mayúscula, 1 dígito, 1 carácter especial, mínimo 6 caracteres
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{6,}$"
    );

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    public static Password fromPlainText(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            throw new ValidationException("La contraseña no puede estar vacía");
        }

        if (!PASSWORD_PATTERN.matcher(plainText).matches()) {
            throw new ValidationException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales");
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
