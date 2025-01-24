package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PortfolioJsonRepositoryImpl extends AbstractJsonRepository<Portfolio, UUID>
    implements PortfolioRepository {

    private final TransactionRepository transactionRepository;

    public PortfolioJsonRepositoryImpl(Gson gson, TransactionRepository transactionRepository) {
        super(
            gson,
            JsonPathFactory.PORTFOLIOS_FILE.getPath(),
            TypeToken.getParameterized(Set.class, Portfolio.class).getType(),
            Portfolio::getId
        );
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<Portfolio> findByName(String name) {
        return findAll(p -> p.getName() != null && p.getName().equalsIgnoreCase(name))
            .stream().findFirst();
    }

    @Override
    public void addTransaction(UUID portfolioId, UUID transactionId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено"));

        transactionRepository.findById(transactionId).ifPresentOrElse(transaction -> {
            Cryptocurrency cryptocurrency = transaction.getCryptocurrency();
            portfolio.addTransaction(
                transaction.getId(),
                cryptocurrency,
                transaction.getAmount(),
                transaction.getTransactionType()
            );
            add(portfolio); // Зберігаємо оновлене портфоліо
        }, () -> {
            throw new IllegalArgumentException("Транзакцію не знайдено.");
        });
    }


    @Override
    public void removeTransaction(UUID portfolioId, UUID transactionId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено"));
        portfolio.removeTransaction(transactionId, transactionRepository);
        add(portfolio); // Оновлюємо портфоліо
    }

    @Override
    public void updateTotalValue(UUID portfolioId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено"));

        portfolio.calculateTotalValue();
        add(portfolio);
    }
}
