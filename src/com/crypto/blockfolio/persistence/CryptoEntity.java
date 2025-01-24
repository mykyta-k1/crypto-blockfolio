package com.crypto.blockfolio.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CryptoEntity implements Identifiable<String> {

    protected final String symbol;
    protected final String name;
    protected transient Set<String> errors;
    protected transient boolean isValid;

    public CryptoEntity(String symbol, String name) {
        errors = new HashSet<>();
        this.symbol = validatedSymbol(symbol);
        this.name = validatedName(name);
    }

    private String validatedName(String name) {
        if (name == null || name.trim().isEmpty()) {
            errors.add("Назва криптовалюти не може бути порожньою.");
        }
        return name.trim();
    }

    private String validatedSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            errors.add("Символ криптовалюти не може бути порожнім.");
        }
        return symbol.trim().toUpperCase();
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public Set<String> getErrors() {
        return errors;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(symbol, name);
    }

    @Override
    public String getIdentifier() {
        return symbol;
    }
}
