package com.crypto.blockfolio.domain;

import com.crypto.blockfolio.persistence.Identifiable;
import java.util.Set;
import java.util.function.Predicate;

public interface Service<E extends Identifiable<ID>, ID> {

    E get(ID id);

    Set<E> getAll();

    Set<E> getAll(Predicate<E> filter);

    E add(E entity);

    boolean remove(E entity);
}
