package com.mottinut.bff.patient.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortOrder {
    ASC("asc", "Ascendente"),
    DESC("desc", "Descendente");

    private final String code;
    private final String description;

    public static SortOrder fromCode(String code) {
        for (SortOrder order : values()) {
            if (order.code.equalsIgnoreCase(code)) {
                return order;
            }
        }
        return ASC; // default
    }
}
