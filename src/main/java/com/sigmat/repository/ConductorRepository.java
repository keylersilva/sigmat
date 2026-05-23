package com.sigmat.repository;

import com.sigmat.model.Conductor;
import com.google.cloud.firestore.DocumentSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class ConductorRepository extends BaseRepository<Conductor> {

    private static final Logger logger = LoggerFactory.getLogger(ConductorRepository.class);

    public ConductorRepository() {
        super("conductores");
    }

    @Override
    protected Conductor fromDocument(DocumentSnapshot document) {
        try {
            Conductor conductor = new Conductor();
            conductor.setId(document.getId());

            if (document.contains("nombre")) conductor.setNombre(document.getString("nombre"));
            else if (document.contains("nombreCompleto")) conductor.setNombre(document.getString("nombreCompleto"));
            if (document.contains("cedula")) conductor.setCedula(document.getString("cedula"));
            if (document.contains("telefono")) conductor.setTelefono(document.getString("telefono"));
            if (document.contains("direccion")) conductor.setDireccion(document.getString("direccion"));
            if (document.contains("tipoVehiculo")) conductor.setTipoVehiculo(document.getString("tipoVehiculo"));
            if (document.contains("ingresosDiarios")) conductor.setIngresosDiarios(document.getDouble("ingresosDiarios"));
            else if (document.contains("ingresosMensuales")) conductor.setIngresosDiarios(document.getDouble("ingresosMensuales"));
            if (document.contains("estado")) conductor.setEstado(document.getString("estado"));
            if (document.contains("observaciones")) conductor.setObservaciones(document.getString("observaciones"));

            String fechaStr = document.getString("fechaRegistro");
            if (fechaStr != null) {
                if (fechaStr.length() >= 10) {
                    conductor.setFechaRegistro(LocalDate.parse(fechaStr.substring(0, 10)));
                } else {
                    conductor.setFechaRegistro(LocalDate.parse(fechaStr));
                }
            }

            return conductor;
        } catch (Exception e) {
            logger.error("Error parsing Conductor document: {}", e.getMessage());
            return null;
        }
    }

    public CompletableFuture<List<Conductor>> findByEstado(String estado) {
        return findAll().thenApply(list ->
            list.stream().filter(c -> estado.equals(c.getEstado())).toList()
        );
    }

    public CompletableFuture<List<Conductor>> findActivos() {
        return findByEstado("ACTIVO");
    }

    public CompletableFuture<List<Conductor>> findMigrados() {
        return findByEstado("MIGRADO");
    }

    public CompletableFuture<Conductor> findByCedula(String cedula) {
        return findAll().thenApply(list ->
            list.stream().filter(c -> cedula.equals(c.getCedula())).findFirst().orElse(null)
        );
    }
}