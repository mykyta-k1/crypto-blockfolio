package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class PortfolioJsonRepositoryImpl extends AbstractJsonRepository<Portfolio>
    implements PortfolioRepository {

    PortfolioJsonRepositoryImpl(Gson gson) {
        super(gson, JsonPathFactory.PORTFOLIOS_FILE.getPath(), TypeToken
            .getParameterized(Set.class, Portfolio.class)
            .getType());
    }

    @Override
    public Optional<Portfolio> findByName(String name) {
        return entities.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public void updatePortfolio(Portfolio portfolio) {
        Optional<Portfolio> existingPortfolio = entities.stream()
            .filter(p -> p.getId().equals(portfolio.getId()))
            .findFirst();

        if (existingPortfolio.isPresent()) {
            entities.remove(existingPortfolio.get());
        }

        entities.add(portfolio);
        saveChanges();
        System.out.println("Портфель з ID " + portfolio.getId() + " успішно оновлено.");
    }

    @Override
    public void addTransactionToPortfolio(UUID portfolioId, Transaction transaction) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(
                () -> new RuntimeException("Портфель з ID " + portfolioId + " не знайдено."));
        portfolio.addTransactions(transaction);
        updatePortfolio(portfolio);
    }

    @Override
    public void updateTransactionInPortfolio(UUID portfolioId, Transaction transaction) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(
                () -> new RuntimeException("Портфель з ID " + portfolioId + " не знайдено."));
        portfolio.delTransactions(transaction);
        portfolio.addTransactions(transaction);
        updatePortfolio(portfolio);
    }

    @Override
    public void removeTransactionFromPortfolio(UUID portfolioId, UUID transactionId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(
                () -> new RuntimeException("Портфель з ID " + portfolioId + " не знайдено."));
        portfolio.getTransactionsList().removeIf(t -> t.getId().equals(transactionId));
        updatePortfolio(portfolio);
    }
}
