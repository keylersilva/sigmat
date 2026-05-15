package com.sigmat.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Conductor extends BaseModel {
    private String nombre;
    private String cedula;
    private String telefono;
    private String direccion;
    private String tipoVehiculo;
    private double ingresosDiarios;
    private String estado;
    private LocalDate fechaRegistro;
    private String observaciones;

    public Conductor() {
        super();
        this.tipoVehiculo = TipoVehiculo.CARRO_MULA.getDescripcion();
        this.estado = EstadoConductor.ACTIVO.name();
        this.ingresosDiarios = 0.0;
        this.fechaRegistro = LocalDate.now();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public TipoVehiculo getTipoVehiculoEnum() {
        return TipoVehiculo.fromString(this.tipoVehiculo);
    }

    public void setTipoVehiculoEnum(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo != null ? tipoVehiculo.getDescripcion() : null;
    }

    public double getIngresosDiarios() {
        return ingresosDiarios;
    }

    public void setIngresosDiarios(double ingresosDiarios) {
        this.ingresosDiarios = ingresosDiarios;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EstadoConductor getEstadoEnum() {
        return estado != null ? EstadoConductor.valueOf(estado) : null;
    }

    public void setEstadoEnum(EstadoConductor estadoConductor) {
        this.estado = estadoConductor != null ? estadoConductor.name() : null;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombre);
        map.put("cedula", cedula);
        map.put("telefono", telefono);
        map.put("direccion", direccion);
        map.put("tipoVehiculo", tipoVehiculo);
        map.put("ingresosDiarios", ingresosDiarios);
        map.put("estado", estado);
        map.put("fechaRegistro", fechaRegistro != null ? fechaRegistro.toString() : null);
        map.put("observaciones", observaciones);
        return map;
    }
}