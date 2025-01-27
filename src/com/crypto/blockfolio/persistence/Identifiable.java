package com.crypto.blockfolio.persistence;

/**
 * Інтерфейс для об'єктів, які мають унікальний ідентифікатор.
 *
 * @param <ID> тип ідентифікатора.
 */
public interface Identifiable<ID> {

    /**
     * Повертає унікальний ідентифікатор об'єкта.
     *
     * @return унікальний ідентифікатор.
     */
    ID getIdentifier();
}

