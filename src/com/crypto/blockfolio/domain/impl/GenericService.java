package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.domain.exception.EntityNotFoundException;
import com.crypto.blockfolio.persistence.Identifiable;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Set;
import java.util.function.Predicate;

class GenericService<E extends Identifiable<ID>, ID> implements Service<E, ID> {

    private final Repository<E, ID> repository;

    public GenericService(Repository<E, ID> repository) {
        this.repository = repository;
    }

    @Override
    public E get(ID id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                "Ми не знайшли запис по вказаному ідентифікатору: " + id));
    }

    @Override
    public Set<E> getAll() {
        return repository.findAll();
    }

    @Override
    public Set<E> getAll(Predicate<E> filter) {
        return repository.findAll(filter);
    }

    @Override
    public E add(E entity) {
        return repository.add(entity);
    }

    @Override
    public boolean remove(E entity) {
        return repository.remove(entity);
    }
}

