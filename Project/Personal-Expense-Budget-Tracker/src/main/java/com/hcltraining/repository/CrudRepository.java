package com.hcltraining.repository;

import java.util.List;
import java.util.Optional;

/** Basic CRUD operations for an entity identified by a stable key. */
public interface CrudRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
}
