package com.blockfolio.crypto.persistence.entity;

import com.blockfolio.crypto.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Portfolio extends Entity<UUID> {

    private final UUID ownerId;
    private final LocalDateTime createdAt;
    private final Map<String, PortfolioCoin> coinsInPortfolio;
    private final Set<UUID> transactionsList;
    private String name;

    public Portfolio(UUID id, UUID ownerId, LocalDateTime createdAt,
        Map<String, PortfolioCoin> coinsInPortfolio, Set<UUID> transactionsList, String name) {
        super(id);
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.coinsInPortfolio = coinsInPortfolio;
        this.transactionsList = transactionsList;
        this.name = name;
    }
}
