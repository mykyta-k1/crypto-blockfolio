package com.crypto.blockfolio.presentation;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.entity.TransactionType;
import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.RepositoryFactory;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Main {

    public static void main(String[] args) throws NotImplementedException {
        // Отримуємо фабрику JSON репозиторіїв
        RepositoryFactory repositoryFactory = RepositoryFactory.getRepositoryFactory(
            RepositoryFactory.JSON);

        // Отримуємо репозиторії
        UserRepository userRepository = repositoryFactory.getUserRepository();
        CryptocurrencyRepository cryptocurrencyRepository = repositoryFactory.getCryptocurrencyRepository();
        PortfolioRepository portfolioRepository = repositoryFactory.getPortfolioRepository();
        TransactionRepository transactionRepository = repositoryFactory.getTransactionRepository();

        // Створюємо об'єкти
        User user = new User(UUID.randomUUID(), "StrongPass123", "testuser", "test@example.com");
        Cryptocurrency bitcoin = new Cryptocurrency(UUID.randomUUID(), "Bitcoin", 30000.0);
        Cryptocurrency ethereum = new Cryptocurrency(UUID.randomUUID(), "Ethereum", 1500.0);
        Portfolio portfolio = new Portfolio(UUID.randomUUID(), user, "My Portfolio", null, null);
        Transaction transaction = new Transaction(
            UUID.randomUUID(),
            bitcoin,
            TransactionType.BUY,
            BigDecimal.valueOf(60000.0),
            null,
            BigDecimal.valueOf(50.0),
            "Purchase of Bitcoin",
            LocalDateTime.now()
        );

        // Додаємо об'єкти у відповідні репозиторії
        userRepository.add(user);
        cryptocurrencyRepository.add(bitcoin);
        cryptocurrencyRepository.add(ethereum);
        portfolioRepository.add(portfolio);
        transactionRepository.add(transaction);

        // Виконуємо commit, щоб зберегти дані у файли
        repositoryFactory.commit();

        // Виводимо підтвердження
        System.out.println("Дані збережено у JSON файли.");
    }
}
