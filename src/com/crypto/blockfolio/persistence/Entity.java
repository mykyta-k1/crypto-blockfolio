package com.crypto.blockfolio.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Базовий клас для сутностей, які мають унікальний ідентифікатор. Забезпечує основні методи для
 * перевірки валідності та роботи з ідентифікатором.
 */
public class Entity implements Identifiable<UUID> {

    /**
     * Унікальний ідентифікатор сутності.
     */
    protected final UUID id;

    /**
     * Набір для зберігання повідомлень про помилки. Позначений як transient, щоб уникнути
     * серіалізації.
     */
    protected transient Set<String> errors;

    /**
     * Поле для перевірки валідності об'єкта. Позначене як transient, щоб уникнути серіалізації.
     */
    protected transient boolean isValid;

    /**
     * Конструктор для створення сутності з унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор.
     */
    public Entity(UUID id) {
        errors = new HashSet<>();
        this.id = id;
    }

    /**
     * Повертає унікальний ідентифікатор сутності.
     *
     * @return унікальний ідентифікатор.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Перевіряє, чи є об'єкт валідним.
     *
     * @return {@code true}, якщо об'єкт валідний, інакше {@code false}.
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Повертає набір повідомлень про помилки.
     *
     * @return набір помилок.
     */
    public Set<String> getErrors() {
        return errors;
    }

    /**
     * Перевизначає метод equals для порівняння об'єктів за ідентифікатором.
     *
     * @param o об'єкт для порівняння.
     * @return {@code true}, якщо об'єкти рівні, інакше {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    /**
     * Повертає хеш-код об'єкта на основі його ідентифікатора.
     *
     * @return хеш-код.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Повертає унікальний ідентифікатор об'єкта.
     *
     * @return ідентифікатор.
     */
    @Override
    public UUID getIdentifier() {
        return id;
    }
}