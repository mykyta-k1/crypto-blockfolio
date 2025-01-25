package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PortfolioRepository extends Repository<Portfolio, UUID> {

    Optional<Portfolio> findByName(String name);

    Optional<Cryptocurrency> findCryptocurrencyBySymbol(String symbol);

    void addCryptocurrency(UUID portfolioId, String cryptocurrencySymbol, BigDecimal amount);

    void removeCryptocurrency(UUID portfolioId, String cryptocurrencySymbol);

    void addTransaction(UUID portfolioId, UUID transactionId);

    void removeTransaction(UUID portfolioId, UUID transactionId);

    Set<UUID> getTransactions(UUID portfolioId);

    BigDecimal calculateTotalValue(UUID portfolioId);

    Set<String> getTrackedCryptocurrencies(UUID portfolioId);

    void update(Portfolio portfolio);
}
