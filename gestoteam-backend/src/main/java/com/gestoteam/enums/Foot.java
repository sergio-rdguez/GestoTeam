package com.gestoteam.enums;

public enum Foot {
    DIESTRO("Diestro"),
    ZURDO("Zurdo"),
    AMBIDIESTRO("Ambidiestro");

    private final String descripcion;

    Foot(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
