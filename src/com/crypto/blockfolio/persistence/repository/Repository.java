package com.crypto.blockfolio.persistence.repository;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface Repository<E, ID> {

    Optional<E> findById(ID id);

    Set<E> findAll();

    Set<E> findAll(Predicate<E> filter);

    E add(E entity);

    boolean remove(E entity);
}
