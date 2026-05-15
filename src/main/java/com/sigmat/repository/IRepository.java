package com.sigmat.repository;

import com.sigmat.model.BaseModel;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IRepository<T extends BaseModel> {
    CompletableFuture<String> create(T entity);
    CompletableFuture<Optional<T>> findById(String id);
    CompletableFuture<List<T>> findAll();
    CompletableFuture<Boolean> update(String id, T entity);
    CompletableFuture<Boolean> delete(String id);
}