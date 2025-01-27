package com.crypto.blockfolio.persistence.repository.impl.json;

import java.util.List;
import java.util.Optional;

/**
 * Загальний інтерфейс репозиторію для базових CRUD-операцій.
 *
 * @param <T>  тип об'єкта, що зберігається у репозиторії.
 * @param <ID> тип ідентифікатора об'єкта.
 */
public interface GenericRepository<T, ID> {

    /**
     * Знаходить об'єкт за його ідентифікатором.
     *
     * @param id ідентифікатор об'єкта.
     * @return {@link Optional}, що містить об'єкт, якщо він знайдений.
     */
    Optional<T> findById(ID id);

    /**
     * Додає новий об'єкт до репозиторію.
     *
     * @param entity об'єкт для додавання.
     */
    void add(T entity);

    /**
     * Оновлює існуючий об'єкт у репозиторії.
     *
     * @param entity об'єкт для оновлення.
     */
    void update(T entity);

    /**
     * Видаляє об'єкт із репозиторію за його ідентифікатором.
     *
     * @param id ідентифікатор об'єкта, який потрібно видалити.
     */
    void delete(ID id);

    /**
     * Повертає список усіх об'єктів, що зберігаються у репозиторії.
     *
     * @return список об'єктів.
     */
    List<T> findAll();
}


