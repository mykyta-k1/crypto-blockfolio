package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Інтерфейс репозиторію для роботи з об'єктами {@link Portfolio}. Забезпечує методи для пошуку,
 * оновлення та управління криптовалютами і транзакціями у портфелях.
 */
public interface PortfolioRepository extends Repository<Portfolio, UUID> {

    /**
     * Знаходить портфель за його назвою.
     *
     * @param name назва портфеля.
     * @return {@link Optional}, що містить портфель, якщо його знайдено.
     */
    Optional<Portfolio> findByName(String name);

    /**
     * Знаходить криптовалюту в портфелі за її символом.
     *
     * @param symbol символ криптовалюти.
     * @return {@link Optional}, що містить криптовалюту, якщо її знайдено.
     */
    Optional<Cryptocurrency> findCryptocurrencyBySymbol(String symbol);

    /**
     * Додає криптовалюту до портфеля.
     *
     * @param portfolioId          ідентифікатор портфеля.
     * @param cryptocurrencySymbol символ криптовалюти.
     * @param amount               кількість криптовалюти для додавання.
     */
    void addCryptocurrency(UUID portfolioId, String cryptocurrencySymbol, BigDecimal amount);

    /**
     * Видаляє криптовалюту з портфеля.
     *
     * @param portfolioId          ідентифікатор портфеля.
     * @param cryptocurrencySymbol символ криптовалюти.
     */
    void removeCryptocurrency(UUID portfolioId, String cryptocurrencySymbol);

    /**
     * Додає транзакцію до портфеля.
     *
     * @param portfolioId   ідентифікатор портфеля.
     * @param transactionId ідентифікатор транзакції.
     */
    void addTransaction(UUID portfolioId, UUID transactionId);

    /**
     * Видаляє транзакцію з портфеля.
     *
     * @param portfolioId   ідентифікатор портфеля.
     * @param transactionId ідентифікатор транзакції.
     */
    void removeTransaction(UUID portfolioId, UUID transactionId);

    /**
     * Повертає всі транзакції, пов'язані з портфелем.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return набір ідентифікаторів транзакцій.
     */
    Set<UUID> getTransactions(UUID portfolioId);

    /**
     * Обчислює загальну вартість портфеля.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return загальна вартість портфеля.
     */
    BigDecimal calculateTotalValue(UUID portfolioId);

    /**
     * Повертає список криптовалют, які відстежуються у портфелі.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return набір символів криптовалют.
     */
    Set<String> getTrackedCryptocurrencies(UUID portfolioId);

    /**
     * Оновлює дані портфеля.
     *
     * @param portfolio портфель, який потрібно оновити.
     */
    void update(Portfolio portfolio);
}

