package com.sigmat.service;

import com.sigmat.model.Vehiculo;
import com.sigmat.repository.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class VehiculoService {

    private static final Logger logger = LoggerFactory.getLogger(VehiculoService.class);
    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public CompletableFuture<String> registrarVehiculo(Vehiculo vehiculo) {
        return vehiculoRepository.create(vehiculo);
    }

    public CompletableFuture<List<Vehiculo>> listarVehiculos() {
        return vehiculoRepository.findAll();
    }

    public CompletableFuture<Vehiculo> buscarVehiculo(String id) {
        return vehiculoRepository.findById(id).thenApply(opt -> opt.orElse(null));
    }

    public CompletableFuture<Boolean> actualizarVehiculo(String id, Vehiculo vehiculo) {
        return vehiculoRepository.update(id, vehiculo);
    }

    public CompletableFuture<Boolean> eliminarVehiculo(String id) {
        return vehiculoRepository.delete(id);
    }

    public CompletableFuture<List<Vehiculo>> listarActivos() {
        return vehiculoRepository.findActivos();
    }

    public CompletableFuture<List<Vehiculo>> listarDisponibles() {
        return vehiculoRepository.findDisponibles();
    }
}