package com.gestoteam.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    SENIOR("Senior"),
    JUVENIL("Juvenil"),
    CADETE("Cadete"),
    INFANTIL("Infantil"),
    ALEVIN("Alevín"),
    BENJAMIN("Benjamín"),
    PREBENJAMIN("Prebenjamín");

    private final String name;
}
