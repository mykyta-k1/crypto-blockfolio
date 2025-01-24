package com.crypto.blockfolio.persistence.exception;

import java.util.Set;

public class CryptoRatesArgumentException extends RuntimeException {

    private final Set<String> errors;

    public CryptoRatesArgumentException(Set<String> errors) {
        super("Помилки: " + String.join(", ", errors));
        this.errors = errors;
    }

    public Set<String> getErrors() {
        return errors;
    }
}
