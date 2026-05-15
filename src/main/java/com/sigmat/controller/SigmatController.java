package com.sigmat.controller;

import com.sigmat.dto.EstadisticaDTO;
import com.sigmat.dto.LoginRequest;
import com.sigmat.dto.RecomendacionMigracionDTO;
import com.sigmat.model.Conductor;
import com.sigmat.model.Usuario;
import com.sigmat.model.Vehiculo;
import com.sigmat.service.AuthService;
import com.sigmat.service.ConductorService;
import com.sigmat.service.DashboardService;
import com.sigmat.service.VehiculoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class SigmatController {

    private static final Logger logger = LoggerFactory.getLogger(SigmatController.class);

    private final AuthService authService;
    private final ConductorService conductorService;
    private final VehiculoService vehiculoService;
    private final DashboardService dashboardService;

    private Usuario usuarioSesion;

    public SigmatController(AuthService authService, ConductorService conductorService,
                            VehiculoService vehiculoService, DashboardService dashboardService) {
        this.authService = authService;
        this.conductorService = conductorService;
        this.vehiculoService = vehiculoService;
        this.dashboardService = dashboardService;
    }

    public CompletableFuture<Usuario> iniciarSesion(LoginRequest request) {
        return authService.login(request).thenApply(usuario -> {
            if (usuario != null) {
                this.usuarioSesion = usuario;
            }
            return usuario;
        });
    }

    public void cerrarSesion() {
        usuarioSesion = null;
    }

    public boolean estaAutenticado() {
        return usuarioSesion != null;
    }

    public Usuario getUsuarioSesion() {
        return usuarioSesion;
    }

    public boolean esAdministrador() {
        return usuarioSesion != null && usuarioSesion.isAdministrador();
    }

    public CompletableFuture<String> registrarConductor(Conductor conductor) {
        return conductorService.registrarConductor(conductor);
    }

    public CompletableFuture<List<Conductor>> listarConductores() {
        return conductorService.listarConductores();
    }

    public CompletableFuture<Boolean> actualizarConductor(String id, Conductor conductor) {
        return conductorService.actualizarConductor(id, conductor);
    }

    public CompletableFuture<Boolean> eliminarConductor(String id) {
        return conductorService.eliminarConductor(id);
    }

    public CompletableFuture<String> registrarVehiculo(Vehiculo vehiculo) {
        return vehiculoService.registrarVehiculo(vehiculo);
    }

    public CompletableFuture<List<Vehiculo>> listarVehiculos() {
        return vehiculoService.listarVehiculos();
    }

    public CompletableFuture<Boolean> actualizarVehiculo(String id, Vehiculo vehiculo) {
        return vehiculoService.actualizarVehiculo(id, vehiculo);
    }

    public CompletableFuture<Boolean> eliminarVehiculo(String id) {
        return vehiculoService.eliminarVehiculo(id);
    }

    public CompletableFuture<EstadisticaDTO> obtenerEstadisticas() {
        return dashboardService.obtenerEstadisticas();
    }

    public CompletableFuture<List<RecomendacionMigracionDTO>> obtenerRecomendaciones() {
        return conductorService.analizarMigracion();
    }

    public CompletableFuture<Boolean> migrarConductor(String id) {
        return conductorService.migrarConductor(id);
    }
}