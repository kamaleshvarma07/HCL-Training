package com.hcltraining.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Linked insertion-order storage with a caller-supplied entity ID accessor. */
public final class InMemoryRepository<T, ID> implements CrudRepository<T, ID> {
    private final Map<ID, T> entities = new LinkedHashMap<>();
    private final Function<? super T, ? extends ID> idExtractor;

    public InMemoryRepository(Function<? super T, ? extends ID> idExtractor) {
        this.idExtractor = Objects.requireNonNull(idExtractor, "idExtractor");
    }

    @Override public T save(T entity) {
        Objects.requireNonNull(entity, "entity");
        ID id = Objects.requireNonNull(idExtractor.apply(entity), "entity ID");
        entities.put(id, entity);
        return entity;
    }

    @Override public Optional<T> findById(ID id) {
        return Optional.ofNullable(entities.get(Objects.requireNonNull(id, "id")));
    }

    @Override public List<T> findAll() { return List.copyOf(entities.values()); }

    @Override public boolean deleteById(ID id) {
        return entities.remove(Objects.requireNonNull(id, "id")) != null;
    }
}
