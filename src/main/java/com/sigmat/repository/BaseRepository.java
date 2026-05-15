package com.sigmat.repository;

import com.sigmat.config.FirebaseConfig;
import com.sigmat.model.BaseModel;
import com.google.cloud.firestore.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class BaseRepository<T extends BaseModel> implements IRepository<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String collectionName;

    protected BaseRepository(String collectionName) {
        this.collectionName = collectionName;
    }

    protected Firestore getFirestore() {
        return FirebaseConfig.getFirestore();
    }

    protected CollectionReference getCollection() {
        return getFirestore().collection(collectionName);
    }

    @Override
    public CompletableFuture<String> create(T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (entity.getId() == null || entity.getId().isEmpty()) {
                    DocumentReference docRef = getCollection().document();
                    entity.setId(docRef.getId());
                }
                getCollection().document(entity.getId()).set(entity.toMap()).get();
                logger.info("Created {} with ID: {}", collectionName, entity.getId());
                return entity.getId();
            } catch (Exception e) {
                logger.error("Error creating {}: {}", collectionName, e.getMessage());
                throw new RuntimeException("Failed to create " + collectionName, e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<T>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot document = getCollection().document(id).get().get();
                if (document.exists()) {
                    T entity = fromDocument(document);
                    return Optional.of(entity);
                }
                return Optional.empty();
            } catch (Exception e) {
                logger.error("Error finding {} with ID {}: {}", collectionName, id, e.getMessage());
                return Optional.empty();
            }
        });
    }

    @Override
    public CompletableFuture<List<T>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            List<T> entities = new ArrayList<>();
            try {
                QuerySnapshot querySnapshot = getCollection().get().get();
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    T entity = fromDocument(document);
                    if (entity != null) {
                        entities.add(entity);
                    }
                }
                logger.info("Found {} {} records", entities.size(), collectionName);
            } catch (Exception e) {
                logger.error("Error finding all {}: {}", collectionName, e.getMessage());
            }
            return entities;
        });
    }

    @Override
    public CompletableFuture<Boolean> update(String id, T entity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                entity.actualizarFechaModificacion();
                getCollection().document(id).set(entity.toMap()).get();
                logger.info("Updated {} with ID: {}", collectionName, id);
                return true;
            } catch (Exception e) {
                logger.error("Error updating {} with ID {}: {}", collectionName, id, e.getMessage());
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> delete(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                getCollection().document(id).delete().get();
                logger.info("Deleted {} with ID: {}", collectionName, id);
                return true;
            } catch (Exception e) {
                logger.error("Error deleting {} with ID {}: {}", collectionName, id, e.getMessage());
                return false;
            }
        });
    }

    protected abstract T fromDocument(DocumentSnapshot document);
}