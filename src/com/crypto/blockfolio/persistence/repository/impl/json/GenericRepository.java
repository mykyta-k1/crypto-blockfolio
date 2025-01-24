package com.crypto.blockfolio.persistence.repository.impl.json;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    Optional<T> findById(ID id);

    void add(T entity);

    void update(T entity);

    void delete(ID id);

    List<T> findAll();
}

