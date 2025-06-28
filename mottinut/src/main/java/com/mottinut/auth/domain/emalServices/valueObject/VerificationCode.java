package com.mottinut.auth.domain.emalServices.valueObject;

import jakarta.persistence.Embeddable;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class VerificationCode {
    private String value;

    public static VerificationCode generate() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // Código de 6 dígitos
        return new VerificationCode(String.valueOf(code));
    }

    public static VerificationCode from(String value) {
        return new VerificationCode(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerificationCode that = (VerificationCode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}