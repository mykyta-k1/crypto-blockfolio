package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import com.crypto.blockfolio.persistence.entity.TransactionType;
import java.math.BigDecimal;
import java.util.UUID;

public final class TransactionAddDto extends Entity {

    private final String cryptocurrencySymbol;
    private final TransactionType transactionType;
    private final BigDecimal amount;
    private final BigDecimal costs;
    private final BigDecimal fees;
    private final String description;

    public TransactionAddDto(
        UUID id,
        String cryptocurrencySymbol,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal costs,
        BigDecimal fees,
        String description
    ) {
        super(id);
        this.cryptocurrencySymbol = validateCryptocurrencySymbol(cryptocurrencySymbol);
        this.transactionType = validateTransactionType(transactionType);
        this.amount = validateAmount(amount);
        this.costs = validateCosts(costs);
        this.fees = validateFees(fees);
        this.description = validateDescription(description);

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Некоректні дані транзакції: " + errors);
        }
    }

    private String validateCryptocurrencySymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            errors.add("Символ криптовалюти не може бути порожнім.");
        }
        return symbol != null ? symbol.trim() : null;
    }

    private TransactionType validateTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted("тип транзакції"));
        }
        return transactionType;
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Кількість монет повинна бути більше 0.");
        }
        return amount;
    }

    private BigDecimal validateCosts(BigDecimal costs) {
        if (costs == null || costs.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Витрати повинні бути більше 0.");
        }
        return costs;
    }

    private BigDecimal validateFees(BigDecimal fees) {
        if (fees == null || fees.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Комісії не можуть бути від’ємними.");
        }
        return fees;
    }

    private String validateDescription(String description) {
        if (description != null && description.length() > 256) {
            errors.add("Опис транзакції не може перевищувати 256 символів.");
        }
        return description != null ? description.trim() : null;
    }

    public String getCryptocurrencySymbol() {
        return cryptocurrencySymbol;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getCosts() {
        return costs;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public String getDescription() {
        return description;
    }
}
