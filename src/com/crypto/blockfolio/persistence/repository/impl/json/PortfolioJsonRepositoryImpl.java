package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class PortfolioJsonRepositoryImpl extends
    GenericJsonRepository<Portfolio, UUID> implements PortfolioRepository {

    public PortfolioJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.PORTFOLIOS_FILE.getPath(),
            TypeToken.getParameterized(Set.class, Portfolio.class).getType(),
            Portfolio::getId
        );
    }

    @Override
    public Optional<Portfolio> findByName(String name) {
        return entities.stream()
            .filter(portfolio -> portfolio.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    @Override
    public void addCryptocurrency(UUID portfolioId, String cryptocurrencySymbol,
        BigDecimal amount) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        portfolio.getBalances().merge(cryptocurrencySymbol, amount, BigDecimal::add);
        saveChanges();
    }

    @Override
    public void removeCryptocurrency(UUID portfolioId, String cryptocurrencySymbol) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        portfolio.getBalances().remove(cryptocurrencySymbol);
        saveChanges();
    }

    @Override
    public void addTransaction(UUID portfolioId, UUID transactionId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        if (!portfolio.getTransactionsList().add(transactionId)) {
            throw new IllegalArgumentException("Транзакція вже існує у портфоліо.");
        }
        saveChanges();
    }

    @Override
    public void removeTransaction(UUID portfolioId, UUID transactionId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        if (!portfolio.getTransactionsList().remove(transactionId)) {
            throw new IllegalArgumentException("Транзакцію не знайдено у портфоліо.");
        }
        saveChanges();
    }

    @Override
    public Set<UUID> getTransactions(UUID portfolioId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        return portfolio.getTransactionsList();
    }

    @Override
    public BigDecimal calculateTotalValue(UUID portfolioId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        return portfolio.getBalances().values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Set<String> getTrackedCryptocurrencies(UUID portfolioId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        return portfolio.getBalances().keySet();
    }

    @Override
    public Optional<Cryptocurrency> findCryptocurrencyBySymbol(String symbol) {
        return entities.stream()
            .flatMap(portfolio -> portfolio.getBalances().keySet().stream())
            .filter(s -> s.equalsIgnoreCase(symbol))
            .map(s -> new Cryptocurrency(s, s, 0.0, 0.0, 0.0, 0.0, LocalDateTime.now()))
            .findFirst();
    }

    @Override
    public void update(Portfolio portfolio) {
        UUID id = portfolio.getId();
        entities.removeIf(existingPortfolio -> existingPortfolio.getId().equals(id));
        entities.add(portfolio);
        saveChanges();
    }

}
