package com.gestoteam.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceStatus {
    PRESENT("Presente"),
    ABSENT("Ausente"),
    INJURED("Lesionado"),
    LATE("Retraso"),
    UNJUSTIFIED_ABSENCE("Falta no justificada"),
    JUSTIFIED_ABSENCE("Falta justificada");

    private final String name;
}
