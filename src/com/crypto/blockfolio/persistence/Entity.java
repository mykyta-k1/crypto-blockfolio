package com.crypto.blockfolio.persistence;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Entity implements Identifiable<UUID> {

    protected final UUID id;
    protected transient Set<String> errors;
    protected transient boolean isValid;

    public Entity(UUID id) {
        errors = new HashSet<>();
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public Set<String> getErrors() {
        return errors;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public UUID getIdentifier() {
        return id;
    }
}