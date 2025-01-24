package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class TransactionJsonRepositoryImpl extends AbstractJsonRepository<Transaction, UUID>
    implements TransactionRepository {

    TransactionJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.TRANSACTIONS_FILE.getPath(),
            TypeToken.getParameterized(Set.class, Transaction.class).getType(),
            Transaction::getId
        );
    }

    @Override
    public Optional<Transaction> findByCryptocurrencyId(String cryptocurrencySymbol) {
        return entities.stream()
            .filter(t -> t.getCryptocurrency() != null) // Перевіряємо, що криптовалюта не null
            .filter(
                t -> t.getCryptocurrency().getSymbol() != null) // Перевіряємо, що symbol не null
            .filter(t -> t.getCryptocurrency().getSymbol()
                .equalsIgnoreCase(cryptocurrencySymbol)) // Порівнюємо symbol
            .findFirst();
    }

    @Override
    public void update(Transaction transaction) {
        // Перевіряємо, чи транзакція валідна перед оновленням
        if (!transaction.isValid()) {
            throw new IllegalArgumentException(
                "Транзакція містить невалідні дані: " + transaction.getErrors()
            );
        }

        // Пошук існуючої транзакції
        Optional<Transaction> existingTransaction = findById(transaction.getId());

        if (existingTransaction.isPresent()) {
            entities.remove(existingTransaction.get());
            System.out.println(
                "Існуюча транзакція з ID " + transaction.getId() + " була оновлена."
            );
        } else {
            System.out.println(
                "Транзакція з ID " + transaction.getId() + " не знайдена. Буде додано нову."
            );
        }

        // Додаємо оновлену транзакцію
        entities.add(transaction);
        saveChanges();
        System.out.println("Транзакція з ID " + transaction.getId() + " успішно оновлена.");
    }

    @Override
    public void delete(UUID transactionId) {
        entities.removeIf(t -> t.getId().equals(transactionId));
        saveChanges();
    }

}
