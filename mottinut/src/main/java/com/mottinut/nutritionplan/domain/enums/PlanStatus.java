package com.mottinut.nutritionplan.domain.enums;

import com.mottinut.shared.domain.exceptions.ValidationException;

public enum PlanStatus {
    PENDING_REVIEW("pending_review"),
    APPROVED("approved"),
    REJECTED("rejected"),
    PENDING_PATIENT_ACCEPTANCE("pending_patient_acceptance"),
    ACCEPTED_BY_PATIENT("accepted_by_patient"),
    REJECTED_BY_PATIENT("rejected_by_patient");

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