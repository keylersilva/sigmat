package com.sigmat.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseModel {
    private String id;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    protected BaseModel() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    protected BaseModel(String id) {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public void actualizarFechaModificacion() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (id != null) map.put("id", id);
        if (fechaCreacion != null) map.put("fechaCreacion", fechaCreacion.toString());
        if (fechaActualizacion != null) map.put("fechaActualizacion", fechaActualizacion.toString());
        return map;
    }
}