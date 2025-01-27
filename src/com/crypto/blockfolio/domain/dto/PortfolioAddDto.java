package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для створення нового портфеля. Використовується для передачі даних,
 * необхідних для створення портфеля в системі.
 */
public final class PortfolioAddDto extends Entity {

    /**
     * Ідентифікатор власника портфеля.
     */
    private final UUID ownerId;
    /**
     * Назва портфеля.
     */
    private final String name;
    /**
     * Баланси криптовалют у портфелі (ключ — символ криптовалюти).
     */
    private final Map<String, BigDecimal> balances; // Замінили на String для символів криптовалют
    /**
     * Набір ідентифікаторів транзакцій, пов'язаних із портфелем.
     */
    private final Set<UUID> transactionIds;
    /**
     * Загальна вартість портфеля.
     */
    private BigDecimal totalValue;

    /**
     * Конструктор для створення нового екземпляра {@link PortfolioAddDto}.
     *
     * @param id             унікальний ідентифікатор портфеля.
     * @param ownerId        ідентифікатор власника портфеля.
     * @param name           назва портфеля.
     * @param balances       мапа балансів криптовалют (ключ — символ криптовалюти, значення —
     *                       баланс).
     * @param transactionIds набір ідентифікаторів транзакцій, пов'язаних із портфелем.
     * @throws IllegalArgumentException якщо вхідні дані некоректні.
     */
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

    /**
     * Валідатор для ідентифікатора власника портфеля.
     *
     * @param ownerId ідентифікатор власника.
     * @return перевірений ідентифікатор.
     */
    private UUID validateOwnerId(UUID ownerId) {
        if (ownerId == null) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted("ідентифікатор власника"));
        }
        return ownerId;
    }

    /**
     * Валідатор для назви портфеля.
     *
     * @param name назва портфеля.
     * @return перевірена назва.
     */
    private String validateName(String name) {
        ValidationUtils.validateRequired(name, "назва портфоліо", errors);
        ValidationUtils.validateLength(name, 1, 64, "назва портфоліо", errors);
        ValidationUtils.validatePattern(name, "^[а-яА-ЯёЁa-zA-Z0-9_\\s]+$", "назва портфоліо",
            errors);
        return name;
    }

    /**
     * Валідатор для балансів криптовалют.
     *
     * @param balances мапа балансів криптовалют.
     * @return перевірена мапа.
     */
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

    /**
     * Повертає ідентифікатор власника портфеля.
     *
     * @return ідентифікатор власника.
     */
    public UUID getOwnerId() {
        return ownerId;
    }

    /**
     * Повертає назву портфеля.
     *
     * @return назва портфеля.
     */
    public String getName() {
        return name;
    }

    /**
     * Повертає мапу балансів криптовалют.
     *
     * @return мапа балансів (ключ — символ криптовалюти, значення — баланс).
     */
    public Map<String, BigDecimal> getBalances() {
        return balances;
    }

    /**
     * Повертає набір ідентифікаторів транзакцій, пов'язаних із портфелем.
     *
     * @return набір ідентифікаторів транзакцій.
     */
    public Set<UUID> getTransactionIds() {
        return transactionIds;
    }

    /**
     * Повертає загальну вартість портфеля.
     *
     * @return загальна вартість.
     */
    public BigDecimal getTotalValue() {
        return totalValue;
    }

    /**
     * Встановлює загальну вартість портфеля.
     *
     * @param totalValue нове значення загальної вартості.
     */
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }
}
