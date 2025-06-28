package com.mottinut.bff.patient.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PatientSortField {
    NAME("name", "Nombre"),
    AGE("age", "Edad"),
    CREATED("created", "Fecha de creación");

    private final String code;
    private final String description;

    public static PatientSortField fromCode(String code) {
        for (PatientSortField field : values()) {
            if (field.code.equalsIgnoreCase(code)) {
                return field;
            }
        }
        return NAME; // default
    }
}
