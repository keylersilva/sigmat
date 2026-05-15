package com.sigmat.service;

import com.sigmat.dto.LoginRequest;
import com.sigmat.model.Usuario;
import com.sigmat.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public CompletableFuture<Usuario> login(LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getNombreUsuario());
        return usuarioRepository.authenticate(request.getNombreUsuario(), request.getContrasena())
            .thenApply(usuario -> {
                if (usuario != null) {
                    logger.info("Login successful for user: {}", request.getNombreUsuario());
                } else {
                    logger.warn("Login failed for user: {}", request.getNombreUsuario());
                }
                return usuario;
            });
    }

    public CompletableFuture<Usuario> registrarUsuario(Usuario usuario) {
        return usuarioRepository.findByNombreUsuario(usuario.getNombreUsuario())
            .thenCompose(existente -> {
                if (existente != null) {
                    return CompletableFuture.completedFuture(null);
                }
                return usuarioRepository.create(usuario).thenApply(id -> usuario);
            });
    }
}