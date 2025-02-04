package com.blockfolio.crypto.persistence.entity;

import com.blockfolio.crypto.persistence.Entity;
import com.blockfolio.crypto.persistence.exception.TransactionArgumentException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction extends Entity<UUID> {

    private final UUID portfolioId;
    private LocalDateTime createdAt;
    private String coinId;
    private TransactionType transactionType;
    private BigDecimal totalSpent;
    private BigDecimal quantity;
    private BigDecimal pricePerCoin;
    private BigDecimal fees;
    private String description;

    public Transaction(UUID id, UUID portfolioId, String coinId, TransactionType transactionType,
        BigDecimal totalSpent, BigDecimal quantity, BigDecimal pricePerCoin, BigDecimal fees, String description) {
        super(id);
        this.portfolioId = portfolioId;
        this.coinId = coinId;
        this.transactionType = transactionType;
        this.totalSpent = totalSpent;
        this.quantity = quantity;
        this.pricePerCoin = pricePerCoin;
        this.fees = fees;
        this.description = description;
        this.createdAt = LocalDateTime.now();

        if (!errors.isEmpty()) {
            throw new TransactionArgumentException("Помилка транзакції: " + errors);
        }
    }


}
