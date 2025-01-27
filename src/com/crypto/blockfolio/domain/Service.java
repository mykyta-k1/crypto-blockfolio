package com.crypto.blockfolio.domain;

import com.crypto.blockfolio.persistence.Identifiable;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Загальний сервісний інтерфейс для управління сутностями, які є ідентифікованими.
 *
 * @param <E>  тип сутності, що реалізує інтерфейс {@link Identifiable}.
 * @param <ID> тип ідентифікатора сутності.
 */
public interface Service<E extends Identifiable<ID>, ID> {

    /**
     * Повертає сутність за її ідентифікатором.
     *
     * @param id ідентифікатор сутності.
     * @return сутність, що відповідає заданому ідентифікатору.
     */
    E get(ID id);

    /**
     * Повертає всі доступні сутності.
     *
     * @return набір усіх сутностей.
     */
    Set<E> getAll();

    /**
     * Повертає всі сутності, що відповідають заданому фільтру.
     *
     * @param filter предикат для фільтрації сутностей.
     * @return набір сутностей, що відповідають фільтру.
     */
    Set<E> getAll(Predicate<E> filter);

    /**
     * Додає нову сутність до системи.
     *
     * @param entity сутність для додавання.
     * @return додана сутність.
     */
    E add(E entity);

    /**
     * Видаляє сутність із системи.
     *
     * @param entity сутність для видалення.
     * @return {@code true}, якщо видалення виконано успішно, інакше {@code false}.
     */
    boolean remove(E entity);
}

