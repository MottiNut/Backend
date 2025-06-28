package com.mottinut.patient.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ChronicDiseaseFilter {
    ALL("all", "Todos"),
    DIABETES("diabetes", "Diabetes"),
    HYPERTENSION("hypertension", "Hipertensión"),
    OBESITY("obesity", "Obesidad"),
    CARDIOVASCULAR("cardiovascular", "Cardiovascular"),
    NONE("none", "Sin enfermedad crónica");

    private final String code;
    private final String description;

    public static ChronicDiseaseFilter fromCode(String code) {
        return Arrays.stream(values())
                .filter(filter -> filter.code.equals(code))
                .findFirst()
                .orElse(ALL);
    }
}
