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
import com.crypto.blockfolio.persistence.repository.impl.json.JsonRepositoryFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        // Використовуємо фабрику для створення репозиторіїв
        RepositoryFactory repositoryFactory = JsonRepositoryFactory.getInstance();

        UserRepository userRepository = repositoryFactory.getUserRepository();
        PortfolioRepository portfolioRepository = repositoryFactory.getPortfolioRepository();
        TransactionRepository transactionRepository = repositoryFactory.getTransactionRepository();
        CryptocurrencyRepository cryptocurrencyRepository = repositoryFactory.getCryptocurrencyRepository();

        // 1. Додавання користувача
        UUID userId = UUID.randomUUID();
        User user = new User(userId, "StrongPass1!", "test_user", "test_user@example.com");
        userRepository.add(user);
        System.out.println("Користувача додано: " + user);

        // 2. Додавання портфеля користувачу
        UUID portfolioId = UUID.randomUUID();
        Portfolio portfolio = new Portfolio(portfolioId, userId, "My Portfolio");
        portfolioRepository.add(portfolio);
        user.addPortfolio(portfolioId);
        userRepository.update(user);
        System.out.println("Портфель додано користувачу: " + portfolio);

        // 3. Додавання криптовалюти
        Cryptocurrency bitcoin = new Cryptocurrency("BTC", "Bitcoin", 30000.0, 600_000_000_000.0,
            50_000_000_000.0, 2.5, LocalDateTime.now());
        cryptocurrencyRepository.add(bitcoin);
        System.out.println("Криптовалюту додано: " + bitcoin);

        // 4. Додавання транзакцій до портфеля
        UUID transactionId1 = UUID.randomUUID();
        Transaction transaction1 = new Transaction(transactionId1, bitcoin, TransactionType.BUY,
            BigDecimal.valueOf(0.1), BigDecimal.valueOf(3000), null,
            BigDecimal.valueOf(10), "Перша покупка BTC", LocalDateTime.now());
        transactionRepository.add(transaction1);
        portfolioRepository.addTransaction(portfolioId, transactionId1);
        System.out.println("Транзакцію додано: " + transaction1);

        UUID transactionId2 = UUID.randomUUID();
        Transaction transaction2 = new Transaction(transactionId2, bitcoin, TransactionType.BUY,
            BigDecimal.valueOf(0.05), BigDecimal.valueOf(1500), null,
            BigDecimal.valueOf(5), "Друга покупка BTC", LocalDateTime.now());
        transactionRepository.add(transaction2);
        portfolioRepository.addTransaction(portfolioId, transactionId2);
        System.out.println("Транзакцію додано: " + transaction2);

        // 5. Перевірка балансу і загальної вартості портфеля
        Portfolio updatedPortfolio = portfolioRepository.findById(portfolioId).orElseThrow();
        System.out.println("Оновлений портфель: " + updatedPortfolio);
        System.out.println("Загальна вартість портфеля: " + updatedPortfolio.getTotalValue());

        // 6. Видалення транзакції
        portfolioRepository.removeTransaction(portfolioId, transactionId1);
        System.out.println("Після видалення транзакції 1, оновлений портфель: " +
            portfolioRepository.findById(portfolioId).orElseThrow());

        // 7. Видалення портфеля
        user.removePortfolio(portfolioId);
        userRepository.update(user);
        portfolioRepository.remove(portfolio);
        System.out.println(
            "Після видалення портфеля, користувач: " + userRepository.findById(userId)
                .orElseThrow());

        // 8. Видалення користувача
        userRepository.remove(user);
        System.out.println("Користувача видалено.");
    }

    public static void printTable(String[][] data) {
        // Calculate max width for each column
        int[] maxColumnWidths = new int[data[0].length];
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                maxColumnWidths[i] = Math.max(maxColumnWidths[i], row[i].length());
            }
        }

        // Print the table
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                String col = row[i];
                System.out.printf("%-" + maxColumnWidths[i] + "s", col);
                if (i < row.length - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }
    }
}
