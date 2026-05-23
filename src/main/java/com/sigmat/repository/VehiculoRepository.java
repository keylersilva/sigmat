package com.sigmat.repository;

import com.sigmat.model.Vehiculo;
import com.google.cloud.firestore.DocumentSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class VehiculoRepository extends BaseRepository<Vehiculo> {

    private static final Logger logger = LoggerFactory.getLogger(VehiculoRepository.class);

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
