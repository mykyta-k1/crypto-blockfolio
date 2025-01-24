package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends Repository<Portfolio, UUID> {

    Optional<Portfolio> findByName(String name);

    void addTransaction(UUID portfolioId, UUID transactionId);

    void removeTransaction(UUID portfolioId, UUID transactionId);

    void updateTotalValue(UUID portfolioId);
}
