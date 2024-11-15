package com.gestoteam.enums;

public enum Position {
    POR("Portero"),
    DFC("Defensa Central"),
    LTD("Lateral Derecho"),
    LTI("Lateral Izquierdo"),
    LIB("Líbero"),
    MCD("Medio Centro Defensivo"),
    MC("Medio Centro"),
    MCO("Medio Centro Ofensivo"),
    ED("Extremo Derecho"),
    EI("Extremo Izquierdo"),
    DC("Delantero Centro");

    private final String descripcion;

    Position(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
