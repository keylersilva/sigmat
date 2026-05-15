package com.sigmat.dto;

public class EstadisticaDTO {
    private long totalConductores;
    private long totalVehiculos;
    private long conductoresActivos;
    private long conductoresMigrados;
    private double promedioIngresos;
    private double ingresosTotales;

    public long getTotalConductores() {
        return totalConductores;
    }

    public void setTotalConductores(long totalConductores) {
        this.totalConductores = totalConductores;
    }

    public long getTotalVehiculos() {
        return totalVehiculos;
    }

    public void setTotalVehiculos(long totalVehiculos) {
        this.totalVehiculos = totalVehiculos;
    }

    public long getConductoresActivos() {
        return conductoresActivos;
    }

    public void setConductoresActivos(long conductoresActivos) {
        this.conductoresActivos = conductoresActivos;
    }

    public long getConductoresMigrados() {
        return conductoresMigrados;
    }

    public void setConductoresMigrados(long conductoresMigrados) {
        this.conductoresMigrados = conductoresMigrados;
    }

    public double getPromedioIngresos() {
        return promedioIngresos;
    }

    public void setPromedioIngresos(double promedioIngresos) {
        this.promedioIngresos = promedioIngresos;
    }

    public double getIngresosTotales() {
        return ingresosTotales;
    }

    public void setIngresosTotales(double ingresosTotales) {
        this.ingresosTotales = ingresosTotales;
    }
}