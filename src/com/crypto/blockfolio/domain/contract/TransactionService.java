package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.Reportable;
import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.domain.dto.TransactionAddDto;
import com.crypto.blockfolio.persistence.entity.Transaction;
import java.util.List;
import java.util.UUID;

public interface TransactionService extends Service<Transaction, UUID>, Reportable<Transaction> {

    Transaction addTransaction(TransactionAddDto transactionAddDto);

    Transaction getTransactionById(UUID id);

    List<Transaction> getTransactionsByPortfolioId(UUID portfolioId);

    void deleteTransaction(UUID id);

    void calculatePnL(UUID transactionId);
}
