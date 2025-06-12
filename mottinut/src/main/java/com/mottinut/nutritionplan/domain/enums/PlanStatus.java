package com.mottinut.nutritionplan.domain.enums;

import com.mottinut.shared.domain.exceptions.ValidationException;

public enum PlanStatus {
    PENDING_REVIEW("pending_review"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String value;

    PlanStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PlanStatus fromString(String value) {
        for (PlanStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new ValidationException("Estado de plan inválido: " + value);
    }
}