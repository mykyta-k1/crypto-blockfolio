package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
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
    private final Map<String, BigDecimal> balances; // Замінили на String для символів криптовалют
    private final Set<UUID> transactionIds;
    private BigDecimal totalValue;

    public PortfolioAddDto(UUID id, UUID ownerId, String name,
        Map<String, BigDecimal> balances,
        Set<UUID> transactionIds) {
        super(id);
        this.ownerId = validateOwnerId(ownerId);
        this.name = validateName(name);
        this.totalValue = BigDecimal.ZERO; // Початкове значення
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
        ValidationUtils.validatePattern(name, "^[а-яА-ЯёЁa-zA-Z0-9_\\s]+$", "назва портфоліо",
            errors);
        return name;
    }

    private Map<String, BigDecimal> validateBalances(Map<String, BigDecimal> balances) {
        if (balances == null) {
            return new HashMap<>();
        }

        balances.forEach((symbol, amount) -> {
            if (symbol == null || symbol.trim().isEmpty()) {
                errors.add("Символ криптовалюти не може бути порожнім.");
            }
            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                errors.add("Баланс криптовалюти не може бути від'ємним або null.");
            }
        });

        return new HashMap<>(balances);
    }

    // Гетери та сетери
    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public Map<String, BigDecimal> getBalances() {
        return balances;
    }

    public Set<UUID> getTransactionIds() {
        return transactionIds;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}
