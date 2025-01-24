package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

public class Transaction extends Entity implements Comparable<Transaction> {

    private LocalDateTime createdAt;
    private Cryptocurrency cryptocurrency;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal costs;
    private BigDecimal profit;
    private BigDecimal fees;
    private String description;

    public Transaction(UUID id, Cryptocurrency cryptocurrency, TransactionType transactionType,
        BigDecimal amount, BigDecimal costs, BigDecimal profit, BigDecimal fees, String description,
        LocalDateTime createdAt) {
        super(id);
        setCryptocurrency(cryptocurrency);
        setTransactionType(transactionType);
        setAmount(amount);
        setCosts(costs);
        this.profit = profit;
        this.fees = fees;
        this.description = description;
        this.createdAt = validateCreatedAt(createdAt);

        if (!this.isValid()) {
            System.err.println("Помилки транзакції: " + errors);
            throw new EntityArgumentException(errors);
        }
    }

    @Override
    public int compareTo(Transaction o) {
        return this.createdAt.compareTo(o.createdAt);
    }

    public Cryptocurrency getCryptocurrency() {
        return cryptocurrency;
    }

    public void setCryptocurrency(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || !cryptocurrency.isValid()) {
            errors.add("Криптомонета не є валідною.");
        } else {
            this.cryptocurrency = cryptocurrency;
        }
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            errors.add("Тип транзакції не може бути порожнім.");
        } else if (!EnumSet.allOf(TransactionType.class).contains(transactionType)) {
            errors.add("Такого типу транзакції не існує.");
        } else {
            this.transactionType = transactionType;
        }
    }

    public BigDecimal getCosts() {
        return costs;
    }

    public void setCosts(BigDecimal costs) {
        if (costs == null || costs.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Витрати не можуть бути від'ємними.");
        }
        this.costs = costs;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Кількість монет має бути більше 0.");
        } else {
            this.amount = amount;
        }
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        if (profit != null && profit.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Прибуток не може бути від’ємним.");
        }
        this.profit = profit;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        if (fees == null || fees.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Комісії не можуть бути від'ємними.");
        }
        this.fees = fees;
    }

    public BigDecimal calculatePnl() {
        if (cryptocurrency == null || cryptocurrency.getCurrentPrice() <= 0 || amount == null
            || fees == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal currentValue = BigDecimal.valueOf(cryptocurrency.getCurrentPrice())
            .multiply(amount);

        switch (transactionType) {
            case BUY:
                return currentValue.subtract(costs).subtract(fees)
                    .setScale(2, RoundingMode.HALF_UP);

            case SELL:
                return profit.subtract(costs).subtract(fees).setScale(2, RoundingMode.HALF_UP);

            case TRANSFER_WITHDRAWAL:
                return costs.negate().setScale(2, RoundingMode.HALF_UP);

            case TRANSFER_DEPOSIT:
                return currentValue.setScale(2, RoundingMode.HALF_UP);

            default:
                return BigDecimal.ZERO;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null && description.length() > 256) {
            errors.add("Опис не має бути довшим за 256 символів.");
        }
        this.description = description != null ? description.trim() : null;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = validateCreatedAt(createdAt);
    }

    private LocalDateTime validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null || createdAt.isAfter(LocalDateTime.now())) {
            errors.add("Дата створення не може бути в майбутньому або пустою.");
        }
        return createdAt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "cryptocurrency=" + (cryptocurrency != null ? cryptocurrency.getName() : "null") +
            ", transactionType=" + transactionType +
            ", amount=" + amount +
            ", costs=" + costs +
            ", profit=" + profit +
            ", fees=" + fees +
            ", description='" + description + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}
