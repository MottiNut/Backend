package com.mottinut.nutritionplan.domain.enums;

import com.mottinut.shared.domain.exceptions.ValidationException;

public enum ReviewAction {
    APPROVE("approve"),
    REJECT("reject"),
    EDIT("edit");

    private final String value;

    ReviewAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ReviewAction fromString(String value) {
        for (ReviewAction action : values()) {
            if (action.value.equals(value)) {
                return action;
            }
        }
        throw new ValidationException("Acción de revisión inválida: " + value);
    }
}

