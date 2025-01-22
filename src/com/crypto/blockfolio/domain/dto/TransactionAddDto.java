package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import com.crypto.blockfolio.persistence.entity.TransactionType;
import java.math.BigDecimal;
import java.util.UUID;

public class TransactionAddDto extends Entity {

    private final UUID cryptocurrencyId;
    private final TransactionType transactionType;
    private final BigDecimal costs;
    private final BigDecimal fees;
    private final String description;

    public TransactionAddDto(UUID id, UUID cryptocurrencyId, TransactionType transactionType,
        BigDecimal costs, BigDecimal fees, String description) {
        super(id);
        this.cryptocurrencyId = cryptocurrencyId;
        this.transactionType = validateTransactionType(transactionType);
        this.costs = validateCosts(costs);
        this.fees = validateFees(fees);
        this.description = validateDescription(description);
    }

    private TransactionType validateTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted("тип транзакції"));
        }
        return transactionType;
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

    public UUID cryptocurrencyId() {
        return cryptocurrencyId;
    }

    public TransactionType transactionType() {
        return transactionType;
    }

    public BigDecimal fosts() {
        return costs;
    }

    public BigDecimal fees() {
        return fees;
    }

    public String description() {
        return description;
    }
}
