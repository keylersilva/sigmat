package com.sigmat.dto;

public class RecomendacionMigracionDTO {
    private String conductorId;
    private String nombreConductor;
    private double ingresosActuales;
    private double ingresosEstimados;
    private double mejoraEconomica;
    private String prioridad;
    private String razon;

    public enum Prioridad {
        ALTA, MEDIA, BAJA
    }

    public RecomendacionMigracionDTO() {}

    public String getConductorId() {
        return conductorId;
    }

    public void setConductorId(String conductorId) {
        this.conductorId = conductorId;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public double getIngresosActuales() {
        return ingresosActuales;
    }

    public void setIngresosActuales(double ingresosActuales) {
        this.ingresosActuales = ingresosActuales;
    }

    public double getIngresosEstimados() {
        return ingresosEstimados;
    }

    public void setIngresosEstimados(double ingresosEstimados) {
        this.ingresosEstimados = ingresosEstimados;
    }

    public double getMejoraEconomica() {
        return mejoraEconomica;
    }

    public void setMejoraEconomica(double mejoraEconomica) {
        this.mejoraEconomica = mejoraEconomica;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }
}