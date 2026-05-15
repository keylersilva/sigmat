package com.sigmat.repository;

import com.sigmat.model.Conductor;
import com.google.cloud.firestore.DocumentSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ConductorRepository extends BaseRepository<Conductor> {

    private static final Logger logger = LoggerFactory.getLogger(ConductorRepository.class);
    private static final ConcurrentHashMap<String, Conductor> conductoresDemo = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);

    static {
        agregarConductorDemo("Juan Carlos Pérez", "12345678", "3001234567", "Calle 45 #12-30", "Carro de Mula", 45000, "ACTIVO");
        agregarConductorDemo("María Elena López", "23456789", "3002345678", "Carrera 15 #34-12", "Carro de Mula", 52000, "ACTIVO");
        agregarConductorDemo("Pedro Miguel Torres", "34567890", "3003456789", "Av. Caracas #45-67", "Carro de Mula", 38000, "ACTIVO");
        agregarConductorDemo("Ana María García", "45678901", "3004567890", "Calle 80 #23-45", "Motocarro", 65000, "MIGRADO");
        agregarConductorDemo("Carlos Andrés Ruiz", "56789012", "3005678901", "Carrera 7 #65-23", "Carro de Mula", 48000, "ACTIVO");
    }

    private static void agregarConductorDemo(String nombre, String cedula, String telefono, 
                                              String direccion, String tipoVehiculo, 
                                              double ingresos, String estado) {
        Conductor c = new Conductor();
        c.setId("cond-" + idCounter.getAndIncrement());
        c.setNombre(nombre);
        c.setCedula(cedula);
        c.setTelefono(telefono);
        c.setDireccion(direccion);
        c.setTipoVehiculo(tipoVehiculo);
        c.setIngresosDiarios(ingresos);
        c.setEstado(estado);
        c.setFechaRegistro(LocalDate.now().minusDays((long)(Math.random() * 365)));
        conductoresDemo.put(c.getId(), c);
    }

    public ConductorRepository() {
        super("conductores");
    }

    @Override
    protected Conductor fromDocument(DocumentSnapshot document) {
        try {
            Conductor conductor = new Conductor();
            conductor.setId(document.getId());

            if (document.contains("nombre")) conductor.setNombre(document.getString("nombre"));
            if (document.contains("cedula")) conductor.setCedula(document.getString("cedula"));
            if (document.contains("telefono")) conductor.setTelefono(document.getString("telefono"));
            if (document.contains("direccion")) conductor.setDireccion(document.getString("direccion"));
            if (document.contains("tipoVehiculo")) conductor.setTipoVehiculo(document.getString("tipoVehiculo"));
            if (document.contains("ingresosDiarios")) conductor.setIngresosDiarios(document.getDouble("ingresosDiarios"));
            if (document.contains("estado")) conductor.setEstado(document.getString("estado"));
            if (document.contains("observaciones")) conductor.setObservaciones(document.getString("observaciones"));

            String fechaStr = document.getString("fechaRegistro");
            if (fechaStr != null) conductor.setFechaRegistro(LocalDate.parse(fechaStr));

            return conductor;
        } catch (Exception e) {
            logger.error("Error parsing Conductor document: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public CompletableFuture<String> create(Conductor entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId("cond-" + idCounter.getAndIncrement());
        }
        conductoresDemo.put(entity.getId(), entity);
        logger.info("Conductor creado (demo): {}", entity.getId());
        return CompletableFuture.completedFuture(entity.getId());
    }

    @Override
    public CompletableFuture<java.util.Optional<Conductor>> findById(String id) {
        if (conductoresDemo.containsKey(id)) {
            return CompletableFuture.completedFuture(java.util.Optional.of(conductoresDemo.get(id)));
        }
        return super.findById(id).thenApply(opt -> {
            if (opt.isPresent()) return opt;
            return java.util.Optional.empty();
        });
    }

    @Override
    public CompletableFuture<List<Conductor>> findAll() {
        List<Conductor> lista = new ArrayList<>(conductoresDemo.values());
        return CompletableFuture.completedFuture(lista);
    }

    @Override
    public CompletableFuture<Boolean> update(String id, Conductor entity) {
        if (conductoresDemo.containsKey(id)) {
            entity.actualizarFechaModificacion();
            conductoresDemo.put(id, entity);
            logger.info("Conductor actualizado (demo): {}", id);
            return CompletableFuture.completedFuture(true);
        }
        return super.update(id, entity);
    }

    @Override
    public CompletableFuture<Boolean> delete(String id) {
        if (conductoresDemo.containsKey(id)) {
            conductoresDemo.remove(id);
            logger.info("Conductor eliminado (demo): {}", id);
            return CompletableFuture.completedFuture(true);
        }
        return super.delete(id);
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