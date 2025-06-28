package com.mottinut.nutritionplan.domain.enums;

import com.mottinut.shared.domain.exceptions.ValidationException;

public enum PatientAction {
    ACCEPT("accept"),
    REJECT("reject");

    private final String value;

    PatientAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PatientAction fromString(String value) {
        for (PatientAction action : values()) {
            if (action.value.equals(value)) {
                return action;
            }
        }
        throw new ValidationException("Acción del paciente inválida: " + value);
    }
}
