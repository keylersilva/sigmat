package com.sigmat.utils;

import com.sigmat.model.Usuario;
import java.util.Optional;

public class SesionManager {

    private static SesionManager instancia;
    private Usuario usuarioActual;

    private SesionManager() {}

    public static synchronized SesionManager getInstancia() {
        if (instancia == null) {
            instancia = new SesionManager();
        }
        return instancia;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public boolean estaAutenticado() {
        return usuarioActual != null;
    }

    public Optional<Usuario> getUsuarioActual() {
        return Optional.ofNullable(usuarioActual);
    }

    public boolean esAdministrador() {
        return usuarioActual != null && usuarioActual.isAdministrador();
    }

    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual.getNombreUsuario() : null;
    }
}