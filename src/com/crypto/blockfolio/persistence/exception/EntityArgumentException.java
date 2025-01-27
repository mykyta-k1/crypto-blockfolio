package com.crypto.blockfolio.persistence.exception;

import java.util.Set;

/**
 * Виключення, що вказує на помилки аргументів сутності. Використовується для передачі списку
 * помилок, пов'язаних із валідацією сутності.
 */
public class EntityArgumentException extends RuntimeException {

    /**
     * Набір помилок, пов'язаних із сутністю.
     */
    private final Set<String> errors;

    /**
     * Конструктор для створення нового екземпляра {@link EntityArgumentException}.
     *
     * @param errors набір помилок, які спричинили виключення.
     */
    public EntityArgumentException(Set<String> errors) {
        super("Помилки: " + String.join(", ", errors));
        this.errors = errors;
    }

    /**
     * Повертає набір помилок, пов'язаних із сутністю.
     *
     * @return набір помилок.
     */
    public Set<String> getErrors() {
        return errors;
    }
}

