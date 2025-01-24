package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends Repository<Transaction, UUID> {

    Optional<Transaction> findByCryptocurrencyId(String cryptocurrencyId);

    void update(Transaction transaction);

    void delete(UUID transactionId);
}
