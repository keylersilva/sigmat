package com.sigmat.repository;

import com.google.cloud.firestore.DocumentSnapshot;
import com.sigmat.model.RolUsuario;
import com.sigmat.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Repository
public class UsuarioRepository extends BaseRepository<Usuario> {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioRepository.class);

    public UsuarioRepository() {
        super("usuarios");
    }

    @Override
    protected Usuario fromDocument(DocumentSnapshot document) {
        try {
            Usuario usuario = new Usuario();
            usuario.setId(document.getId());

            if (document.contains("nombreUsuario")) {
                usuario.setNombreUsuario(document.getString("nombreUsuario"));
            }

            if (document.contains("contrasena")) {
                usuario.setContrasena(document.getString("contrasena"));
            }

            if (document.contains("nombreCompleto")) {
                usuario.setNombreCompleto(document.getString("nombreCompleto"));
            }

            if (document.contains("correo")) {
                usuario.setCorreo(document.getString("correo"));
            }

            if (document.contains("rol")) {
                String rol = document.getString("rol");
                if (rol != null) {
                    usuario.setRol(RolUsuario.valueOf(rol));
                }
            }

            if (document.contains("activo")) {
                usuario.setActivo(Boolean.TRUE.equals(document.getBoolean("activo")));
            }

            if (document.contains("ultimoAcceso")) {
                String fecha = document.getString("ultimoAcceso");
                if (fecha != null && !fecha.isEmpty()) {
                    usuario.setUltimoAcceso(LocalDateTime.parse(fecha));
                }
            }

            return usuario;

        } catch (Exception e) {
            logger.error("Error parseando usuario: {}", e.getMessage());
            return null;
        }
    }

    public CompletableFuture<Usuario> findByNombreUsuario(String nombreUsuario) {
        return findAll().thenApply(lista ->
                lista.stream()
                        .filter(u -> nombreUsuario.equals(u.getNombreUsuario()))
                        .findFirst()
                        .orElse(null)
        );
    }

    public CompletableFuture<Usuario> authenticate(String nombreUsuario, String contrasena) {
        logger.info("Intentando autenticacion Firebase para: {}", nombreUsuario);

        return findByNombreUsuario(nombreUsuario)
                .thenApply(usuario -> {
                    if (usuario != null && usuario.isActivo()
                            && contrasena.equals(usuario.getContrasena())) {
                        logger.info("Autenticacion exitosa: {}", nombreUsuario);
                        return usuario;
                    }

                    logger.warn("Credenciales invalidas: {}", nombreUsuario);
                    return null;
                });
    }
}