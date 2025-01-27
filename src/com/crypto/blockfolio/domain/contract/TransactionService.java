package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.Reportable;
import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.domain.dto.CryptocurrencyAddDto;
import com.crypto.blockfolio.domain.dto.TransactionAddDto;
import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import java.util.List;
import java.util.UUID;

/**
 * Інтерфейс TransactionService визначає сервіси для роботи з транзакціями. Забезпечує додавання,
 * оновлення, видалення транзакцій, а також обчислення прибутку (PnL).
 */
public interface TransactionService extends Service<Transaction, UUID>, Reportable<Transaction> {

    /**
     * Додає нову транзакцію на основі переданих даних.
     *
     * @param transactionAddDto        об'єкт {@link TransactionAddDto}, що містить дані
     *                                 транзакції.
     * @param cryptocurrencyRepository репозиторій для роботи з криптовалютами.
     * @return створена транзакція {@link Transaction}.
     */
    Transaction addTransaction(TransactionAddDto transactionAddDto,
        CryptocurrencyRepository cryptocurrencyRepository);

    /**
     * Повертає транзакцію за її ідентифікатором.
     *
     * @param id унікальний ідентифікатор транзакції.
     * @return транзакція {@link Transaction}, що відповідає вказаному ідентифікатору.
     */
    Transaction getTransactionById(UUID id);

    /**
     * Повертає список транзакцій, що належать до портфеля з вказаним ідентифікатором.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return список транзакцій {@link List<Transaction>}.
     */
    List<Transaction> getTransactionsByPortfolioId(UUID portfolioId);

    /**
     * Видаляє транзакцію за її ідентифікатором.
     *
     * @param id унікальний ідентифікатор транзакції.
     */
    void deleteTransaction(UUID id);

    /**
     * Обчислює прибуток/збиток (PnL) для транзакції з вказаним ідентифікатором.
     *
     * @param transactionId ідентифікатор транзакції.
     */
    void calculatePnL(UUID transactionId);

    /**
     * Оновлює інформацію про криптовалюту.
     *
     * @param symbol                   символ криптовалюти (наприклад, BTC).
     * @param updatedCryptocurrencyDto об'єкт {@link CryptocurrencyAddDto}, що містить оновлені дані
     *                                 криптовалюти.
     */
    void updateCryptocurrency(String symbol, CryptocurrencyAddDto updatedCryptocurrencyDto);

    /**
     * Оновлює існуючу транзакцію за її ідентифікатором.
     *
     * @param transactionId         ідентифікатор транзакції.
     * @param updatedTransactionDto об'єкт {@link TransactionAddDto}, що містить оновлені дані
     *                              транзакції.
     */
    void updateTransaction(UUID transactionId, TransactionAddDto updatedTransactionDto);

    /**
     * Додає нову транзакцію на основі переданих даних.
     *
     * @param transactionAddDto об'єкт {@link TransactionAddDto}, що містить дані транзакції.
     * @return створена транзакція {@link Transaction}.
     */
    Transaction addTransaction(TransactionAddDto transactionAddDto);
}

