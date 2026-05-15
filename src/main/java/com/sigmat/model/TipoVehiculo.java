package com.sigmat.model;

public enum TipoVehiculo {
    CARRO_MULA("Carro de Mula"),
    MOTOCARRO("Motocarro");

    private final String descripcion;

    TipoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoVehiculo fromString(String text) {
        if (text == null) return null;
        for (TipoVehiculo tipo : values()) {
            if (tipo.name().equalsIgnoreCase(text) || tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        return null;
    }
}