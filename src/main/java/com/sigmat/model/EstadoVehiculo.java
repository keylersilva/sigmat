package com.sigmat.model;

public enum EstadoVehiculo {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    EN_MANTENIMIENTO("En Mantenimiento"),
    DADO_BAJA("Dado de Baja");

    private final String descripcion;

    EstadoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}