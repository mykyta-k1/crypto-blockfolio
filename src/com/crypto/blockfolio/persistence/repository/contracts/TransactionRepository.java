package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Set;
import java.util.UUID;

public interface TransactionRepository extends Repository<Transaction, UUID> {

    Set<Transaction> findByPortfolioId(UUID portfolioId);

    Set<Transaction> findByCryptocurrencySymbol(String cryptocurrencySymbol);

    void addTransactionToPortfolio(UUID portfolioId, Transaction transaction);

    void removeTransactionFromPortfolio(UUID portfolioId, UUID transactionId);
}
