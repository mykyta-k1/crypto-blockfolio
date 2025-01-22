package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

public class Transaction extends Entity implements Comparable<Transaction> {

    private Cryptocurrency cryptocurrency;
    private TransactionType transactionType;
    private BigDecimal costs;
    private BigDecimal profit;
    private BigDecimal fees;
    private String description;
    private LocalDateTime createAt;

    public Transaction(UUID id, Cryptocurrency cryptocurrency, TransactionType transactionType,
        BigDecimal costs, BigDecimal profit, BigDecimal fees, String description,
        LocalDateTime createAt) {
        super(id);
        setCryptocurrency(cryptocurrency);
        setTransactionType(transactionType);
        setCosts(costs);
        this.profit = profit;
        this.fees = fees;
        this.description = description;
        this.createAt = validateCreateAt(createAt);

        if (!this.isValid()) {
            System.err.println("Помилки транзакції: " + errors);
            throw new EntityArgumentException(errors);
        }
    }

    @Override
    public int compareTo(Transaction o) {
        return this.createAt.compareTo(o.createAt);
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
        final String templateCosts = "витрат";

        if (costs == null) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted(templateCosts));
        } else if (costs.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Витрати не можуть бути від'ємними.");
        }

        this.costs = costs;
        calculatePnl();
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        if (transactionType == TransactionType.SELL) {
            this.profit = calculatePnl();
        } else {
            this.profit = BigDecimal.ZERO;
        }
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        final String templateFees = "комісії";

        if (fees == null) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted(templateFees));
        } else if (fees.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Комісії не можуть бути від'ємним.");
        }

        this.fees = fees;
    }

    public BigDecimal calculatePnl() {
        if (cryptocurrency == null || cryptocurrency.getCurrentPrice() <= 0 || costs == null
            || fees == null) {
            return BigDecimal.ZERO; // Повертаємо 0, якщо дані для розрахунку неповні.
        }

        switch (transactionType) {
            case BUY:
                // Для купівлі PNL розраховується як unrealized profit/loss
                BigDecimal currentValue = BigDecimal.valueOf(cryptocurrency.getCurrentPrice())
                    .multiply(BigDecimal.valueOf(cryptocurrency.getCount()));
                return currentValue
                    .subtract(costs)
                    .subtract(fees)
                    .setScale(2, RoundingMode.HALF_UP);

            case SELL:
                // Для продажу PNL = profit - costs - fees
                return profit
                    .subtract(costs)
                    .subtract(fees)
                    .setScale(2, RoundingMode.HALF_UP);

            case TRANSFER_WITHDRAWAL:
                return costs.negate().setScale(2, RoundingMode.HALF_UP);

            case TRANSFER_DEPOSIT:
                return BigDecimal.valueOf(cryptocurrency.getCurrentPrice())
                    .multiply(BigDecimal.valueOf(cryptocurrency.getCount()))
                    .setScale(2, RoundingMode.HALF_UP);

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

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    private LocalDateTime validateCreateAt(LocalDateTime createAt) {
        if (createAt == null || createAt.isAfter(LocalDateTime.now())) {
            errors.add("Дата створення не може бути в майбутньому або пустою.");
        }

        return createAt;
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "cryptocurrency=" + (cryptocurrency != null ? cryptocurrency.getName() : "null") +
            ", transactionType=" + transactionType +
            ", costs=" + costs +
            ", profit=" + profit +
            ", fees=" + fees +
            ", description='" + description + '\'' +
            ", createAt=" + createAt +
            '}';
    }
}
