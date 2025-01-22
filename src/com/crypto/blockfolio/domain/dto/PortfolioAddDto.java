package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.util.UUID;

public class PortfolioAddDto extends Entity {

    private final UUID ownerId;
    private final String name;

    public PortfolioAddDto(UUID id, UUID ownerId, String name) {
        super(id);
        this.ownerId = validatedOwnerId(ownerId);
        this.name = validatedName(name);
    }

    private String validatedName(String name) {
        ValidationUtils.validateRequired(name, "назва портфоліо", errors);
        ValidationUtils.validateLength(name, 1, 64, "назва портфоліо", errors);
        ValidationUtils.validatePattern(name, "^[a-zA-Z0-9_]+$", "назва портфоліо", errors);
        return name;
    }

    private UUID validatedOwnerId(UUID ownerId) {
        if (ownerId == null) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted("ідентифікатор власника"));
        }
        return ownerId;
    }

    public UUID ownerId() {
        return ownerId;
    }

    public String name() {
        return name;
    }
}
