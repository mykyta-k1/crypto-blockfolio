package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Set;
import java.util.UUID;

/**
 * Інтерфейс репозиторію для роботи з об'єктами {@link Transaction}. Забезпечує методи для пошуку,
 * додавання, видалення та оновлення транзакцій.
 */
public interface TransactionRepository extends Repository<Transaction, UUID> {

    /**
     * Знаходить усі транзакції, пов'язані з певним портфелем.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return набір транзакцій, пов'язаних із вказаним портфелем.
     */
    Set<Transaction> findByPortfolioId(UUID portfolioId);

    /**
     * Знаходить усі транзакції для певної криптовалюти за її символом.
     *
     * @param cryptocurrencySymbol символ криптовалюти.
     * @return набір транзакцій, що відповідають вказаному символу криптовалюти.
     */
    Set<Transaction> findByCryptocurrencySymbol(String cryptocurrencySymbol);

    /**
     * Додає транзакцію до вказаного портфеля.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @param transaction транзакція, яку потрібно додати.
     */
    void addTransactionToPortfolio(UUID portfolioId, Transaction transaction);

    /**
     * Видаляє транзакцію з вказаного портфеля.
     *
     * @param portfolioId   ідентифікатор портфеля.
     * @param transactionId ідентифікатор транзакції, яку потрібно видалити.
     */
    void removeTransactionFromPortfolio(UUID portfolioId, UUID transactionId);

    /**
     * Оновлює дані вказаної транзакції.
     *
     * @param transactionId      ідентифікатор транзакції, яку потрібно оновити.
     * @param updatedTransaction оновлена транзакція.
     */
    void updateTransaction(UUID transactionId, Transaction updatedTransaction);
}

