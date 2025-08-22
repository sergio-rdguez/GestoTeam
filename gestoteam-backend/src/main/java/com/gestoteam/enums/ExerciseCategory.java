package com.gestoteam.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExerciseCategory {
    CALENTAMIENTO("Calentamiento"),
    TECNICO("Técnico"),
    TACTICO("Táctico"),
    FISICO("Físico"),
    PARTIDO_MODIFICADO("Partido Modificado"),
    TRANSICION("Transición"),
    FINALIZACION("Finalización"),
    POSESION("Posesión"),
    PRESSING("Pressing"),
    OTRO("Otro");

    private final String name;
}
