package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class PortfolioAddDto extends Entity {

    private final UUID ownerId;
    private final String name;
    private final Map<Cryptocurrency, BigDecimal> balances;
    private final Set<UUID> transactionIds;

    public PortfolioAddDto(UUID id, UUID ownerId, String name,
        Map<Cryptocurrency, BigDecimal> balances,
        Set<UUID> transactionIds) {
        super(id);
        this.ownerId = validateOwnerId(ownerId);
        this.name = validateName(name);
        this.balances = validateBalances(balances);
        this.transactionIds = transactionIds != null ? Set.copyOf(transactionIds) : Set.of();

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Некоректні дані портфеля: " + errors);
        }
    }

    private UUID validateOwnerId(UUID ownerId) {
        if (ownerId == null) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted("ідентифікатор власника"));
        }
        return ownerId;
    }

    private String validateName(String name) {
        ValidationUtils.validateRequired(name, "назва портфоліо", errors);
        ValidationUtils.validateLength(name, 1, 64, "назва портфоліо", errors);
        ValidationUtils.validatePattern(name, "[A-Za-z0-9_ ]+", "назва портфоліо", errors);
        return name;
    }

    private Map<Cryptocurrency, BigDecimal> validateBalances(
        Map<Cryptocurrency, BigDecimal> balances) {
        if (balances == null) {
            return new HashMap<>();
        }

        balances.forEach((crypto, amount) -> {
            if (crypto == null || !crypto.isValid()) {
                errors.add("Криптовалюта в балансі не є валідною.");
            }
            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Баланс криптовалюти не може бути від'ємним або null.");
            }
        });

        return new HashMap<>(balances);
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public Map<Cryptocurrency, BigDecimal> getBalances() {
        return balances;
    }

    public Set<UUID> getTransactionIds() {
        return transactionIds;
    }
    
}
