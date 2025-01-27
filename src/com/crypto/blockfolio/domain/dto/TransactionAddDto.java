package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import com.crypto.blockfolio.persistence.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для додавання нової транзакції. Використовується для передачі даних,
 * необхідних для створення транзакції в системі.
 */
public final class TransactionAddDto extends Entity {

    /**
     * Символ криптовалюти, яка бере участь у транзакції.
     */
    private final String cryptocurrencySymbol;
    /**
     * Ідентифікатор портфеля, до якого відноситься транзакція.
     */
    private final UUID portfolioId;
    /**
     * Тип транзакції (купівля, продаж тощо).
     */
    private final TransactionType transactionType;
    /**
     * Кількість криптовалюти в транзакції.
     */
    private final BigDecimal amount;
    /**
     * Сума витрат, пов'язаних із транзакцією.
     */
    private final BigDecimal costs;
    /**
     * Комісія за транзакцію.
     */
    private final BigDecimal fees;
    /**
     * Опис транзакції.
     */
    private final String description;
    /**
     * Час створення транзакції.
     */
    private final LocalDateTime createdAt;

    /**
     * Конструктор для створення нового екземпляра {@link TransactionAddDto}.
     *
     * @param id                   унікальний ідентифікатор транзакції.
     * @param portfolioId          ідентифікатор портфеля, до якого відноситься транзакція.
     * @param cryptocurrencySymbol символ криптовалюти, що бере участь у транзакції.
     * @param transactionType      тип транзакції (купівля, продаж тощо).
     * @param amount               кількість криптовалюти.
     * @param costs                витрати, пов'язані з транзакцією.
     * @param fees                 комісія за транзакцію.
     * @param description          опис транзакції.
     * @throws IllegalArgumentException якщо вхідні дані некоректні.
     */
    public TransactionAddDto(
        UUID id,
        UUID portfolioId,
        String cryptocurrencySymbol,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal costs,
        BigDecimal fees,
        String description
    ) {
        super(id);
        this.portfolioId = portfolioId;
        this.cryptocurrencySymbol = validateCryptocurrencySymbol(cryptocurrencySymbol);
        this.transactionType = validateTransactionType(transactionType);
        this.amount = validateAmount(amount);
        this.costs = validateCosts(costs);
        this.fees = validateFees(fees);
        this.description = validateDescription(description);
        this.createdAt = LocalDateTime.now();

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Некоректні дані транзакції: " + errors);
        }
    }

    /**
     * Перевіряє та повертає символ криптовалюти.
     *
     * @param symbol символ криптовалюти для перевірки.
     * @return перевірений символ криптовалюти.
     */
    private String validateCryptocurrencySymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            errors.add("Символ криптовалюти не може бути порожнім.");
        }
        return symbol != null ? symbol.trim() : null;
    }

    /**
     * Перевіряє та повертає тип транзакції.
     *
     * @param transactionType тип транзакції для перевірки.
     * @return перевірений тип транзакції.
     */
    private TransactionType validateTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted("тип транзакції"));
        }
        return transactionType;
    }

    /**
     * Перевіряє та повертає кількість криптовалюти.
     *
     * @param amount кількість криптовалюти для перевірки.
     * @return перевірена кількість криптовалюти.
     */
    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Кількість монет повинна бути більше 0.");
        }
        return amount;
    }

    /**
     * Перевіряє та повертає витрати.
     *
     * @param costs витрати для перевірки.
     * @return перевірені витрати.
     */
    private BigDecimal validateCosts(BigDecimal costs) {
        if (costs == null || costs.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Витрати не можуть бути від’ємними.");
        }
        return costs != null ? costs : BigDecimal.ZERO;
    }

    /**
     * Перевіряє та повертає комісію.
     *
     * @param fees комісія для перевірки.
     * @return перевірена комісія.
     */
    private BigDecimal validateFees(BigDecimal fees) {
        if (fees == null || fees.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Комісії не можуть бути від’ємними.");
        }
        return fees;
    }

    /**
     * Перевіряє та повертає опис транзакції.
     *
     * @param description опис для перевірки.
     * @return перевірений опис.
     */
    private String validateDescription(String description) {
        if (description != null && description.length() > 256) {
            errors.add("Опис транзакції не може перевищувати 256 символів.");
        }
        return description != null ? description.trim() : null;
    }

    /**
     * Повертає символ криптовалюти.
     *
     * @return символ криптовалюти.
     */
    public String getCryptocurrencySymbol() {
        return cryptocurrencySymbol;
    }

    /**
     * Повертає тип транзакції.
     *
     * @return тип транзакції.
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Повертає кількість криптовалюти в транзакції.
     *
     * @return кількість криптовалюти.
     */
    public BigDecimal getAmount() {
        return amount;
    }


    /**
     * Повертає витрати, пов'язані з транзакцією.
     *
     * @return витрати.
     */
    public BigDecimal getCosts() {
        return costs;
    }

    /**
     * Повертає комісію за транзакцію.
     *
     * @return комісія.
     */
    public BigDecimal getFees() {
        return fees;
    }

    /**
     * Повертає опис транзакції.
     *
     * @return опис.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Повертає ідентифікатор портфеля.
     *
     * @return ідентифікатор портфеля.
     */
    public UUID getPortfolioId() {
        return portfolioId;
    }

    /**
     * Повертає час створення транзакції.
     *
     * @return час створення.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
