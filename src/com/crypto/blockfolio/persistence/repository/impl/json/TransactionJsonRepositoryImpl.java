package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class TransactionJsonRepositoryImpl
    extends GenericJsonRepository<Transaction, UUID>
    implements TransactionRepository {

    public TransactionJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.TRANSACTIONS_FILE.getPath(),
            TypeToken.getParameterized(Set.class, Transaction.class).getType(),
            Transaction::getId
        );
    }

    @Override
    public Set<Transaction> findByPortfolioId(UUID portfolioId) {
        return entities.stream()
            .filter(transaction -> transaction.getPortfolioId().equals(portfolioId))
            .collect(Collectors.toSet());
    }

    @Override
    public Set<Transaction> findByCryptocurrencySymbol(String cryptocurrencySymbol) {
        return entities.stream()
            .filter(transaction -> transaction.getCryptocurrency() != null &&
                transaction.getCryptocurrency().getSymbol().equalsIgnoreCase(cryptocurrencySymbol))
            .collect(Collectors.toSet());
    }

    @Override
    public void addTransactionToPortfolio(UUID portfolioId, Transaction transaction) {
        if (transaction == null || portfolioId == null) {
            throw new IllegalArgumentException("Транзакція або ID портфеля не можуть бути null.");
        }

        transaction.setPortfolioId(portfolioId);
        add(transaction);
        saveChanges();
    }

    @Override
    public void removeTransactionFromPortfolio(UUID portfolioId, UUID transactionId) {
        Transaction transaction = findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Транзакція не знайдена."));

        if (!transaction.getPortfolioId().equals(portfolioId)) {
            throw new IllegalArgumentException("Транзакція не належить вказаному портфелю.");
        }

        remove(transaction);
        saveChanges();
    }
}
