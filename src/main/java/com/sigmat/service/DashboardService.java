package com.sigmat.service;

import com.sigmat.dto.EstadisticaDTO;
import com.sigmat.repository.ConductorRepository;
import com.sigmat.repository.VehiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    private final ConductorRepository conductorRepository;
    private final VehiculoRepository vehiculoRepository;

    public DashboardService(ConductorRepository conductorRepository, VehiculoRepository vehiculoRepository) {
        this.conductorRepository = conductorRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public CompletableFuture<EstadisticaDTO> obtenerEstadisticas() {
        return conductorRepository.findAll().thenCombine(vehiculoRepository.findAll(), (conductores, vehiculos) -> {
            EstadisticaDTO dto = new EstadisticaDTO();
            dto.setTotalConductores(conductores.size());
            dto.setTotalVehiculos(vehiculos.size());
            dto.setConductoresActivos(conductores.stream().filter(c -> "ACTIVO".equals(c.getEstado())).count());
            dto.setConductoresMigrados(conductores.stream().filter(c -> "MIGRADO".equals(c.getEstado())).count());
            
            double suma = conductores.stream().mapToDouble(com.sigmat.model.Conductor::getIngresosDiarios).sum();
            dto.setIngresosTotales(suma);
            dto.setPromedioIngresos(conductores.isEmpty() ? 0 : suma / conductores.size());
            
            return dto;
        });
    }
}