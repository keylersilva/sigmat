package com.sigmat.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Usuario extends BaseModel {
    private String nombreUsuario;
    private String contrasena;
    private String nombreCompleto;
    private String correo;
    private RolUsuario rol;
    private boolean activo;
    private LocalDateTime ultimoAcceso;

    public Usuario() {
        super();
        this.rol = RolUsuario.OPERADOR;
        this.activo = true;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public boolean isAdministrador() {
        return rol == RolUsuario.ADMINISTRADOR;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("nombreUsuario", nombreUsuario);
        map.put("contrasena", contrasena);
        map.put("nombreCompleto", nombreCompleto);
        map.put("correo", correo);
        map.put("rol", rol != null ? rol.name() : null);
        map.put("activo", activo);
        map.put("ultimoAcceso", ultimoAcceso != null ? ultimoAcceso.toString() : null);
        return map;
    }

    public static Usuario fromMap(Map<String, Object> map, String id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombreUsuario((String) map.get("nombreUsuario"));
        usuario.setContrasena((String) map.get("contrasena"));
        usuario.setNombreCompleto((String) map.get("nombreCompleto"));
        usuario.setCorreo((String) map.get("correo"));
        String rolStr = (String) map.get("rol");
        if (rolStr != null) {
            usuario.setRol(RolUsuario.valueOf(rolStr));
        }
        Boolean activo = (Boolean) map.get("activo");
        if (activo != null) usuario.setActivo(activo);
        return usuario;
    }
}