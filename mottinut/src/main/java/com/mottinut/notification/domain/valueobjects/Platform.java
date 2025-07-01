package com.mottinut.notification.domain.valueobjects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Platform {
    ANDROID("android"),
    IOS("ios"),
    WEB("web");

    private final String value;
}