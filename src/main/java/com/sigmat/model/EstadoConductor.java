package com.sigmat.model;

public enum EstadoConductor {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    EN_MIGRACION("En Migración"),
    MIGRADO("Migrado");

    private final String descripcion;

    EstadoConductor(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}