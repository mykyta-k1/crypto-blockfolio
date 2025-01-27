package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Реалізація репозиторію для роботи з транзакціями у форматі JSON. Забезпечує збереження, пошук,
 * оновлення та видалення транзакцій.
 */
public final class TransactionJsonRepositoryImpl
    extends GenericJsonRepository<Transaction, UUID>
    implements TransactionRepository {

    /**
     * Конструктор для ініціалізації репозиторію транзакцій.
     *
     * @param gson об'єкт для серіалізації та десеріалізації JSON.
     */
    public TransactionJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.TRANSACTIONS_FILE.getPath(),
            TypeToken.getParameterized(Set.class, Transaction.class).getType(),
            Transaction::getId
        );
    }

    /**
     * Знаходить всі транзакції, пов'язані з певним портфелем.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return набір транзакцій, пов'язаних із вказаним портфелем.
     */
    @Override
    public Set<Transaction> findByPortfolioId(UUID portfolioId) {
        return entities.stream()
            .filter(transaction -> transaction.getPortfolioId().equals(portfolioId))
            .collect(Collectors.toSet());
    }

    /**
     * Знаходить всі транзакції для певної криптовалюти за її символом.
     *
     * @param cryptocurrencySymbol символ криптовалюти.
     * @return набір транзакцій, що відповідають вказаному символу криптовалюти.
     */
    @Override
    public Set<Transaction> findByCryptocurrencySymbol(String cryptocurrencySymbol) {
        return entities.stream()
            .filter(transaction -> transaction.getCryptocurrency() != null &&
                transaction.getCryptocurrency().getSymbol().equalsIgnoreCase(cryptocurrencySymbol))
            .collect(Collectors.toSet());
    }

    /**
     * Додає транзакцію до портфеля.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @param transaction транзакція, яку потрібно додати.
     * @throws IllegalArgumentException якщо транзакція або ID портфеля є {@code null}.
     */
    @Override
    public void addTransactionToPortfolio(UUID portfolioId, Transaction transaction) {
        if (transaction == null || portfolioId == null) {
            throw new IllegalArgumentException("Транзакція або ID портфеля не можуть бути null.");
        }

        transaction.setPortfolioId(portfolioId);
        add(transaction);
        saveChanges();
    }

    /**
     * Видаляє транзакцію з портфеля.
     *
     * @param portfolioId   ідентифікатор портфеля.
     * @param transactionId ідентифікатор транзакції, яку потрібно видалити.
     * @throws IllegalArgumentException якщо транзакція не знайдена або не належить портфелю.
     */
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

    /**
     * Оновлює дані транзакції у репозиторії.
     *
     * @param transactionId      ідентифікатор транзакції.
     * @param updatedTransaction оновлена транзакція.
     * @throws IllegalArgumentException якщо транзакція не знайдена.
     */
    @Override
    public void updateTransaction(UUID transactionId, Transaction updatedTransaction) {
        Transaction existingTransaction = findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Транзакція не знайдена."));
        existingTransaction.setAmount(updatedTransaction.getAmount());
        existingTransaction.setCosts(updatedTransaction.getCosts());
        existingTransaction.setFees(updatedTransaction.getFees());
        existingTransaction.setTransactionType(updatedTransaction.getTransactionType());
        existingTransaction.setDescription(updatedTransaction.getDescription());
        saveChanges();
    }
}
