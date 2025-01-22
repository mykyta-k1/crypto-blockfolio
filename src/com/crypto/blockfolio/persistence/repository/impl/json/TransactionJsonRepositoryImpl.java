/*
package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class TransactionJsonRepositoryImpl extends AbstractJsonRepository<Transaction>
    implements TransactionRepository {

    TransactionJsonRepositoryImpl(Gson gson) {
        super(gson, JsonPathFactory.PORTFOLIOS_FILE.getPath(), TypeToken
            .getParameterized(Set.class, Transaction.class)
            .getType());
    }

    @Override
    public Optional<Transaction> findByCryptocurrencyId(UUID cryptocurrencyId) {
        return entities.stream()
            .filter(t -> t.getCryptocurrency() != null && t.getCryptocurrency().getId()
                .equals(cryptocurrencyId))
            .findFirst();
    }

    @Override
    public void updateTransaction(String portfolioId, Transaction transaction) {
        // Пошук транзакції за її ID
        Optional<Transaction> existingTransaction = entities.stream()
            .filter(t -> t.getId().equals(transaction.getId()))
            .findFirst();

        if (existingTransaction.isPresent()) {
            entities.remove(existingTransaction.get());
        } else {
            System.out.println(
                "Увага: транзакція з ID " + transaction.getId() + " не знайдена. Додаємо нову.");
        }

        // Додавання (або оновлення) транзакції
        entities.add(transaction);
        saveChanges();
        System.out.println("Транзакцію з ID " + transaction.getId() + " успішно оновлено.");
    }

}
*/
