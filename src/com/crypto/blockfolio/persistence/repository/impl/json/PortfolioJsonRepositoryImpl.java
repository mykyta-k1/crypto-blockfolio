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

/**
 * Реалізація репозиторію для роботи з портфелями у форматі JSON. Забезпечує збереження, пошук,
 * оновлення та управління портфелями.
 */
public final class PortfolioJsonRepositoryImpl extends
    GenericJsonRepository<Portfolio, UUID> implements PortfolioRepository {

    /**
     * Конструктор для ініціалізації репозиторію портфелів.
     *
     * @param gson об'єкт для серіалізації та десеріалізації JSON.
     */
    public PortfolioJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.PORTFOLIOS_FILE.getPath(),
            TypeToken.getParameterized(Set.class, Portfolio.class).getType(),
            Portfolio::getId
        );
    }

    /**
     * Знаходить портфель за його назвою.
     *
     * @param name назва портфеля.
     * @return {@link Optional}, що містить портфель, якщо його знайдено.
     */
    @Override
    public Optional<Portfolio> findByName(String name) {
        return entities.stream()
            .filter(portfolio -> portfolio.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    /**
     * Додає криптовалюту до портфеля.
     *
     * @param portfolioId          ідентифікатор портфеля.
     * @param cryptocurrencySymbol символ криптовалюти.
     * @param amount               кількість криптовалюти для додавання.
     */
    @Override
    public void addCryptocurrency(UUID portfolioId, String cryptocurrencySymbol,
        BigDecimal amount) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        portfolio.getBalances().merge(cryptocurrencySymbol, amount, BigDecimal::add);
        saveChanges();
    }

    /**
     * Видаляє криптовалюту з портфеля.
     *
     * @param portfolioId          ідентифікатор портфеля.
     * @param cryptocurrencySymbol символ криптовалюти.
     */
    @Override
    public void removeCryptocurrency(UUID portfolioId, String cryptocurrencySymbol) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        portfolio.getBalances().remove(cryptocurrencySymbol);
        saveChanges();
    }

    /**
     * Додає транзакцію до портфеля.
     *
     * @param portfolioId   ідентифікатор портфеля.
     * @param transactionId ідентифікатор транзакції.
     */
    @Override
    public void addTransaction(UUID portfolioId, UUID transactionId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        if (!portfolio.getTransactionsList().add(transactionId)) {
            throw new IllegalArgumentException("Транзакція вже існує у портфоліо.");
        }
        saveChanges();
    }

    /**
     * Видаляє транзакцію з портфеля.
     *
     * @param portfolioId   ідентифікатор портфеля.
     * @param transactionId ідентифікатор транзакції.
     */
    @Override
    public void removeTransaction(UUID portfolioId, UUID transactionId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        if (!portfolio.getTransactionsList().remove(transactionId)) {
            throw new IllegalArgumentException("Транзакцію не знайдено у портфоліо.");
        }
        saveChanges();
    }

    /**
     * Повертає всі транзакції, пов'язані з портфелем.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return набір ідентифікаторів транзакцій.
     */
    @Override
    public Set<UUID> getTransactions(UUID portfolioId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        return portfolio.getTransactionsList();
    }

    /**
     * Обчислює загальну вартість портфеля.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return загальна вартість портфеля.
     */
    @Override
    public BigDecimal calculateTotalValue(UUID portfolioId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        return portfolio.getBalances().values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Повертає список криптовалют, які відстежуються у портфелі.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return набір символів криптовалют.
     */
    @Override
    public Set<String> getTrackedCryptocurrencies(UUID portfolioId) {
        Portfolio portfolio = findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException("Портфоліо не знайдено."));

        return portfolio.getBalances().keySet();
    }

    /**
     * Знаходить криптовалюту у портфелях за її символом.
     *
     * @param symbol символ криптовалюти.
     * @return {@link Optional}, що містить криптовалюту, якщо її знайдено.
     */
    @Override
    public Optional<Cryptocurrency> findCryptocurrencyBySymbol(String symbol) {
        return entities.stream()
            .flatMap(portfolio -> portfolio.getBalances().keySet().stream())
            .filter(s -> s.equalsIgnoreCase(symbol))
            .map(s -> new Cryptocurrency(s, s, 0.0, 0.0, 0.0, 0.0, LocalDateTime.now()))
            .findFirst();
    }

    /**
     * Оновлює дані портфеля у репозиторії.
     *
     * @param portfolio портфель для оновлення.
     */
    @Override
    public void update(Portfolio portfolio) {
        UUID id = portfolio.getId();
        entities.removeIf(existingPortfolio -> existingPortfolio.getId().equals(id));
        entities.add(portfolio);
        saveChanges();
    }

}
