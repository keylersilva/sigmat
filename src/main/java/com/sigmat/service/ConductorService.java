package com.sigmat.service;

import com.sigmat.dto.RecomendacionMigracionDTO;
import com.sigmat.model.Conductor;
import com.sigmat.model.TipoVehiculo;
import com.sigmat.repository.ConductorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ConductorService {

    private static final Logger logger = LoggerFactory.getLogger(ConductorService.class);
    private final ConductorRepository conductorRepository;

    public ConductorService(ConductorRepository conductorRepository) {
        this.conductorRepository = conductorRepository;
    }

    public CompletableFuture<String> registrarConductor(Conductor conductor) {
        return conductorRepository.create(conductor);
    }

    public CompletableFuture<List<Conductor>> listarConductores() {
        return conductorRepository.findAll();
    }

    public CompletableFuture<Conductor> buscarConductor(String id) {
        return conductorRepository.findById(id).thenApply(opt -> opt.orElse(null));
    }

    public CompletableFuture<Boolean> actualizarConductor(String id, Conductor conductor) {
        return conductorRepository.update(id, conductor);
    }

    public CompletableFuture<Boolean> eliminarConductor(String id) {
        return conductorRepository.delete(id);
    }

    public CompletableFuture<List<Conductor>> listarActivos() {
        return conductorRepository.findActivos();
    }

    public CompletableFuture<List<RecomendacionMigracionDTO>> analizarMigracion() {
        return conductorRepository.findActivos().thenApply(conductores -> {
            List<RecomendacionMigracionDTO> recomendaciones = new ArrayList<>();
            double promedioIngresos = conductores.stream()
                .mapToDouble(Conductor::getIngresosDiarios)
                .average().orElse(40000);

            for (Conductor c : conductores) {
                if (TipoVehiculo.CARRO_MULA.getDescripcion().equals(c.getTipoVehiculo())) {
                    RecomendacionMigracionDTO dto = new RecomendacionMigracionDTO();
                    dto.setConductorId(c.getId());
                    dto.setNombreConductor(c.getNombre());
                    dto.setIngresosActuales(c.getIngresosDiarios());
                    dto.setIngresosEstimados(c.getIngresosDiarios() * 1.25);
                    dto.setMejoraEconomica(c.getIngresosDiarios() * 0.25);

                    if (c.getIngresosDiarios() >= promedioIngresos * 1.3) {
                        dto.setPrioridad("ALTA");
                        dto.setRazon("Ingresos superiores al promedio");
                    } else if (c.getIngresosDiarios() >= promedioIngresos * 0.9) {
                        dto.setPrioridad("MEDIA");
                        dto.setRazon("Ingresos cercanos al promedio");
                    } else {
                        dto.setPrioridad("BAJA");
                        dto.setRazon("Ingresos por debajo del promedio");
                    }
                    recomendaciones.add(dto);
                }
            }
            return recomendaciones.stream()
                .sorted(Comparator.comparing(r -> {
                    if ("ALTA".equals(r.getPrioridad())) return 0;
                    if ("MEDIA".equals(r.getPrioridad())) return 1;
                    return 2;
                }))
                .toList();
        });
    }

    public CompletableFuture<Boolean> migrarConductor(String id) {
        return conductorRepository.findById(id).thenApply(opt -> {
            if (opt.isPresent()) {
                Conductor conductor = opt.get();
                conductor.setTipoVehiculo(TipoVehiculo.MOTOCARRO.getDescripcion());
                conductor.setEstado("MIGRADO");
                return conductorRepository.update(id, conductor).join();
            }
            return false;
        });
    }
}