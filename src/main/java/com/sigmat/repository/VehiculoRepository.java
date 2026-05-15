package com.sigmat.repository;

import com.sigmat.model.Vehiculo;
import com.google.cloud.firestore.DocumentSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class VehiculoRepository extends BaseRepository<Vehiculo> {

    private static final Logger logger = LoggerFactory.getLogger(VehiculoRepository.class);
    private static final ConcurrentHashMap<String, Vehiculo> vehiculosDemo = new ConcurrentHashMap<>();
    private static final AtomicLong idCounter = new AtomicLong(1);

    static {
        agregarVehiculoDemo("Carro de Mula", "ABC-123", "Pedro Miguel Torres", "ACTIVO", "En uso diario");
        agregarVehiculoDemo("Carro de Mula", "DEF-456", "María Elena López", "ACTIVO", "Buen estado");
        agregarVehiculoDemo("Carro de Mula", "GHI-789", "Carlos Andrés Ruiz", "ACTIVO", "Requiere mantenimiento menor");
        agregarVehiculoDemo("Motocarro", "JKL-012", "Ana María García", "ACTIVO", "Vehículo nuevo");
        agregarVehiculoDemo("Carro de Mula", "MNO-345", null, "EN_MANTENIMIENTO", "Cambio de ruedas");
        agregarVehiculoDemo("Carro de Mula", "PQR-678", "Juan Carlos Pérez", "ACTIVO", "En operación");
    }

    private static void agregarVehiculoDemo(String tipo, String placa, String conductor, String estado, String observaciones) {
        Vehiculo v = new Vehiculo();
        v.setId("veh-" + idCounter.getAndIncrement());
        v.setTipoVehiculo(tipo);
        v.setPlaca(placa);
        v.setPropietario(conductor);
        v.setEstado(estado);
        v.setConductorAsignado(conductor);
        v.setObservaciones(observaciones);
        vehiculosDemo.put(v.getId(), v);
    }

    public VehiculoRepository() {
        super("vehiculos");
    }

    @Override
    protected Vehiculo fromDocument(DocumentSnapshot document) {
        try {
            Vehiculo vehiculo = new Vehiculo();
            vehiculo.setId(document.getId());

            if (document.contains("tipoVehiculo")) vehiculo.setTipoVehiculo(document.getString("tipoVehiculo"));
            if (document.contains("placa")) vehiculo.setPlaca(document.getString("placa"));
            if (document.contains("estado")) vehiculo.setEstado(document.getString("estado"));
            if (document.contains("observaciones")) vehiculo.setObservaciones(document.getString("observaciones"));
            if (document.contains("propietario")) vehiculo.setPropietario(document.getString("propietario"));
            if (document.contains("conductorAsignado")) vehiculo.setConductorAsignado(document.getString("conductorAsignado"));

            return vehiculo;
        } catch (Exception e) {
            logger.error("Error parsing Vehiculo document: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public CompletableFuture<String> create(Vehiculo entity) {
        if (entity.getId() == null || entity.getId().isEmpty()) {
            entity.setId("veh-" + idCounter.getAndIncrement());
        }
        vehiculosDemo.put(entity.getId(), entity);
        logger.info("Vehiculo creado (demo): {}", entity.getId());
        return CompletableFuture.completedFuture(entity.getId());
    }

    @Override
    public CompletableFuture<java.util.Optional<Vehiculo>> findById(String id) {
        if (vehiculosDemo.containsKey(id)) {
            return CompletableFuture.completedFuture(java.util.Optional.of(vehiculosDemo.get(id)));
        }
        return super.findById(id).thenApply(opt -> {
            if (opt.isPresent()) return opt;
            return java.util.Optional.empty();
        });
    }

    @Override
    public CompletableFuture<List<Vehiculo>> findAll() {
        List<Vehiculo> lista = new ArrayList<>(vehiculosDemo.values());
        return CompletableFuture.completedFuture(lista);
    }

    @Override
    public CompletableFuture<Boolean> update(String id, Vehiculo entity) {
        if (vehiculosDemo.containsKey(id)) {
            entity.actualizarFechaModificacion();
            vehiculosDemo.put(id, entity);
            logger.info("Vehiculo actualizado (demo): {}", id);
            return CompletableFuture.completedFuture(true);
        }
        return super.update(id, entity);
    }

    @Override
    public CompletableFuture<Boolean> delete(String id) {
        if (vehiculosDemo.containsKey(id)) {
            vehiculosDemo.remove(id);
            logger.info("Vehiculo eliminado (demo): {}", id);
            return CompletableFuture.completedFuture(true);
        }
        return super.delete(id);
    }

    public CompletableFuture<List<Vehiculo>> findByEstado(String estado) {
        return findAll().thenApply(list ->
            list.stream().filter(v -> estado.equals(v.getEstado())).toList()
        );
    }

    public CompletableFuture<List<Vehiculo>> findActivos() {
        return findByEstado("ACTIVO");
    }

    public CompletableFuture<List<Vehiculo>> findDisponibles() {
        return findAll().thenApply(list ->
            list.stream().filter(v -> "ACTIVO".equals(v.getEstado()) && v.getConductorAsignado() == null).toList()
        );
    }

    public CompletableFuture<Vehiculo> findByPlaca(String placa) {
        return findAll().thenApply(list ->
            list.stream().filter(v -> placa.equalsIgnoreCase(v.getPlaca())).findFirst().orElse(null)
        );
    }
}