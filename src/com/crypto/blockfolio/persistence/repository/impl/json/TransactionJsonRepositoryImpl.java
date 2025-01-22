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
        super(gson, JsonPathFactory.TRANSACTIONS_FILE.getPath(), TypeToken
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
}
