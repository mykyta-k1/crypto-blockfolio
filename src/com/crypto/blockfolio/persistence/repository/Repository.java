package com.crypto.blockfolio.persistence.repository;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Базовий інтерфейс репозиторію для роботи з об'єктами зберігання. Забезпечує основні операції для
 * доступу, пошуку, додавання та видалення об'єктів.
 *
 * @param <E>  тип об'єкта, який зберігається у репозиторії.
 * @param <ID> тип ідентифікатора об'єкта.
 */
public interface Repository<E, ID> {

    /**
     * Знаходить об'єкт за його ідентифікатором.
     *
     * @param id ідентифікатор об'єкта.
     * @return {@link Optional}, що містить об'єкт, якщо його знайдено.
     */
    Optional<E> findById(ID id);

    /**
     * Повертає всі об'єкти, що зберігаються у репозиторії.
     *
     * @return набір усіх об'єктів.
     */
    Set<E> findAll();

    /**
     * Повертає всі об'єкти, які відповідають заданому фільтру.
     *
     * @param filter предикат для фільтрації об'єктів.
     * @return набір об'єктів, що відповідають фільтру.
     */
    Set<E> findAll(Predicate<E> filter);

    /**
     * Додає новий об'єкт у репозиторій. Якщо об'єкт вже існує, він оновлюється.
     *
     * @param entity об'єкт для додавання.
     * @return доданий або оновлений об'єкт.
     */
    E add(E entity);

    /**
     * Видаляє об'єкт із репозиторію.
     *
     * @param entity об'єкт для видалення.
     * @return {@code true}, якщо об'єкт успішно видалено, інакше {@code false}.
     */
    boolean remove(E entity);
}

