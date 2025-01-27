package com.crypto.blockfolio.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Базовий клас для представлення криптовалют. Забезпечує валідацію символу та назви криптовалюти, а
 * також основні методи доступу до даних.
 */
public class CryptoEntity implements Identifiable<String> {

    /**
     * Символ криптовалюти (унікальний ідентифікатор).
     */
    protected final String symbol;

    /**
     * Назва криптовалюти.
     */
    protected final String name;

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
     * Конструктор для створення криптовалюти з символом і назвою. Виконує валідацію даних під час
     * ініціалізації.
     *
     * @param symbol символ криптовалюти.
     * @param name   назва криптовалюти.
     */
    public CryptoEntity(String symbol, String name) {
        errors = new HashSet<>();
        this.symbol = validatedSymbol(symbol);
        this.name = validatedName(name);
    }

    /**
     * Виконує валідацію назви криптовалюти.
     *
     * @param name назва криптовалюти для перевірки.
     * @return перевірена назва криптовалюти.
     */
    private String validatedName(String name) {
        if (name == null || name.trim().isEmpty()) {
            errors.add("Назва криптовалюти не може бути порожньою.");
        }
        return name.trim();
    }

    /**
     * Виконує валідацію символу криптовалюти.
     *
     * @param symbol символ криптовалюти для перевірки.
     * @return перевірений символ криптовалюти у верхньому регістрі.
     */
    private String validatedSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            errors.add("Символ криптовалюти не може бути порожнім.");
        }
        return symbol.trim().toUpperCase();
    }

    /**
     * Повертає символ криптовалюти.
     *
     * @return символ криптовалюти.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Повертає назву криптовалюти.
     *
     * @return назва криптовалюти.
     */
    public String getName() {
        return name;
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
     * Перевіряє, чи є об'єкт валідним.
     *
     * @return {@code true}, якщо об'єкт валідний, інакше {@code false}.
     */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /**
     * Перевизначає метод equals для порівняння об'єктів за символом і назвою.
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
        CryptoEntity that = (CryptoEntity) o;
        return Objects.equals(symbol, that.symbol) && Objects.equals(name, that.name);
    }

    /**
     * Повертає хеш-код об'єкта.
     *
     * @return хеш-код.
     */
    @Override
    public int hashCode() {
        return Objects.hash(symbol, name);
    }

    /**
     * Повертає унікальний ідентифікатор об'єкта (символ криптовалюти).
     *
     * @return унікальний ідентифікатор.
     */
    @Override
    public String getIdentifier() {
        return symbol;
    }
}
