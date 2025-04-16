package com.gestoteam.enums;

public enum Position {
    POR("Portero", 0),
    DFC("Defensa Central", 1),
    LTD("Lateral Derecho", 2),
    LTI("Lateral Izquierdo", 3),
    LIB("Líbero", 4),
    MCD("Medio Centro Defensivo", 5),
    MC("Medio Centro", 6),
    MCO("Medio Centro Ofensivo", 7),
    ED("Extremo Derecho", 8),
    EI("Extremo Izquierdo", 9),
    DC("Delantero Centro", 10),
    NA("No Aplica", 11);

    private final String descripcion;
    private final int order;

    Position(String descripcion, int order) {
        this.descripcion = descripcion;
        this.order = order;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getOrder() {
        return order;
    }
}
