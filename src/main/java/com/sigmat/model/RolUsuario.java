package com.sigmat.model;

public enum RolUsuario {
    ADMINISTRADOR,
    OPERADOR;

    public String getDescripcion() {
        return switch (this) {
            case ADMINISTRADOR -> "Administrador";
            case OPERADOR -> "Operador";
        };
    }
}