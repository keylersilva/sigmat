package com.sigmat.model;

import java.util.HashMap;
import java.util.Map;

public class Vehiculo extends BaseModel {
    private String tipoVehiculo;
    private String placa;
    private String estado;
    private String observaciones;
    private String propietario;
    private String conductorAsignado;

    public Vehiculo() {
        super();
        this.tipoVehiculo = TipoVehiculo.CARRO_MULA.getDescripcion();
        this.estado = EstadoVehiculo.ACTIVO.name();
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

    public void setTipoVehiculoEnum(TipoVehiculo tipo) {
        this.tipoVehiculo = tipo != null ? tipo.getDescripcion() : null;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EstadoVehiculo getEstadoEnum() {
        return estado != null ? EstadoVehiculo.valueOf(estado) : null;
    }

    public void setEstadoEnum(EstadoVehiculo estadoVehiculo) {
        this.estado = estadoVehiculo != null ? estadoVehiculo.name() : null;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getConductorAsignado() {
        return conductorAsignado;
    }

    public void setConductorAsignado(String conductorAsignado) {
        this.conductorAsignado = conductorAsignado;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("tipoVehiculo", tipoVehiculo);
        map.put("placa", placa);
        map.put("estado", estado);
        map.put("observaciones", observaciones);
        map.put("propietario", propietario);
        map.put("conductorAsignado", conductorAsignado);
        return map;
    }

    public static Vehiculo fromMap(Map<String, Object> map, String id) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(id);
        vehiculo.setTipoVehiculo((String) map.get("tipoVehiculo"));
        vehiculo.setPlaca((String) map.get("placa"));
        vehiculo.setEstado((String) map.get("estado"));
        vehiculo.setObservaciones((String) map.get("observaciones"));
        vehiculo.setPropietario((String) map.get("propietario"));
        vehiculo.setConductorAsignado((String) map.get("conductorAsignado"));
        return vehiculo;
    }
}