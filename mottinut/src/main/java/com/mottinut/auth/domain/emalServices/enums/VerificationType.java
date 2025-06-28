package com.mottinut.auth.domain.emalServices.enums;

public enum VerificationType {
    EMAIL("email"),
    SMS("sms"),
    WHATSAPP("whatsapp");

    private final String value;

    VerificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
