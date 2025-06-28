package com.mottinut.shared.domain.exceptions;

public class ValidationException extends BusinessException {
    public ValidationException(String message) {
        super(message);
    }
}