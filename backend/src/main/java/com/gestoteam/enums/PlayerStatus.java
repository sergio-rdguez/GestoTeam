package com.gestoteam.enums;

public enum PlayerStatus {
    ACTIVO("Activo"),
    LESIONADO("Lesionado"),
    SUSPENDIDO("Suspendido"),
    RETIRADO("Retirado");

    private final String descripcion;

    PlayerStatus(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
