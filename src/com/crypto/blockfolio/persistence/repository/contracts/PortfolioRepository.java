package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepository extends Repository<Portfolio> {

    Optional<Portfolio> findByName(String name);

    void updatePortfolio(Portfolio portfolio);

    void addTransactionToPortfolio(UUID portfolioId, Transaction transaction);

    void updateTransactionInPortfolio(UUID portfolioId, Transaction transaction);

    void removeTransactionFromPortfolio(UUID portfolioId, UUID transactionId);
}
