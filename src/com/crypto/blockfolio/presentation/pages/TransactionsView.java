package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.presentation.ApplicationContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class TransactionsView {

    private final Portfolio portfolio;
    private final Scanner scanner;

    public TransactionsView(UUID portfolioId) {
        this.portfolio = ApplicationContext.getPortfolioService().getPortfolioById(portfolioId);
        this.scanner = new Scanner(System.in);
    }

    public void display() {
        System.out.println("\n=== Створення транзакції ===");
        String selectedCrypto = null;
        String transactionType = null;
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal costs = BigDecimal.ZERO;
        BigDecimal fees = BigDecimal.ZERO;
        String description = null;

        while (true) {
            System.out.println(
                "\n[1] Криптовалюта: " + (selectedCrypto != null ? selectedCrypto : "Не вибрано"));
            System.out.println("[2] Тип транзакції: " + (transactionType != null ? transactionType
                : "Не вибрано"));
            System.out.println("[3] Кількість: " + amount);
            System.out.println("[4] Витрати: " + costs);
            System.out.println("[5] Комісія: " + fees);
            System.out.println("[6] Опис: " + (description != null ? description : "Не вказано"));
            System.out.println("[7] Підтвердити та створити транзакцію");
            System.out.println("[0] Повернутися назад");
            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "0":
                    return; // Повернення до попереднього меню
                case "1":
                    selectedCrypto = selectCryptocurrency();
                    break;
                case "2":
                    transactionType = selectTransactionType();
                    break;
                case "3":
                    amount = inputBigDecimal("Введіть кількість:");
                    break;
                case "4":
                    costs = inputBigDecimal("Введіть витрати (сума операції):");
                    break;
                case "5":
                    fees = inputBigDecimal("Введіть комісію:");
                    break;
                case "6":
                    System.out.print("Введіть опис: ");
                    description = scanner.nextLine().trim();
                    break;
                case "7":
                    if (selectedCrypto == null || transactionType == null
                        || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        System.out.println("Помилка: Усі обов'язкові поля мають бути заповнені.");
                    } else {
                        createTransaction(selectedCrypto, transactionType, amount, costs, fees,
                            description);
                        return;
                    }
                    break;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private String selectCryptocurrency() {
        System.out.println("\nОберіть криптовалюту з портфеля:");
        List<String> cryptos = new ArrayList<>(portfolio.getBalances().keySet());
        for (int i = 0; i < cryptos.size(); i++) {
            System.out.printf("[%d] %s%n", i + 1, cryptos.get(i));
        }
        int choice = inputInt("Ваш вибір:");
        if (choice > 0 && choice <= cryptos.size()) {
            return cryptos.get(choice - 1);
        } else {
            System.out.println("Невірний вибір. Спробуйте ще раз.");
            return null;
        }
    }

    private String selectTransactionType() {
        System.out.println("\nОберіть тип транзакції:");
        System.out.println("[1] Купівля");
        System.out.println("[2] Продаж");
        System.out.println("[3] Поповнення");
        System.out.println("[4] Виведення");
        int choice = inputInt("Ваш вибір:");
        return switch (choice) {
            case 1 -> "BUY";
            case 2 -> "SELL";
            case 3 -> "DEPOSIT";
            case 4 -> "WITHDRAW";
            default -> {
                System.out.println("Невірний вибір. Спробуйте ще раз.");
                yield null;
            }
        };
    }

    private void createTransaction(String crypto, String type, BigDecimal amount, BigDecimal costs,
        BigDecimal fees, String description) {
        BigDecimal currentBalance = portfolio.getBalances().getOrDefault(crypto, BigDecimal.ZERO);

        switch (type) {
            case "BUY", "DEPOSIT" -> {
                portfolio.getBalances().put(crypto, currentBalance.add(amount));
                System.out.printf("Баланс криптовалюти %s збільшено на %.2f.%n", crypto, amount);
            }
            case "SELL", "WITHDRAW" -> {
                if (currentBalance.compareTo(amount) < 0) {
                    System.out.println("Помилка: Недостатньо балансу для цієї транзакції.");
                    return;
                }
                portfolio.getBalances().put(crypto, currentBalance.subtract(amount));
                System.out.printf("Баланс криптовалюти %s зменшено на %.2f.%n", crypto, amount);
            }
        }

        // Оновлення загальної вартості портфеля
        ApplicationContext.getPortfolioService().calculateTotalValue(portfolio.getId());

        System.out.println("Транзакція успішно створена!");
    }

    private BigDecimal inputBigDecimal(String prompt) {
        System.out.print(prompt + " ");
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Некоректне число. Спробуйте ще раз: ");
            }
        }
    }

    private int inputInt(String prompt) {
        System.out.print(prompt + " ");
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Некоректне число. Спробуйте ще раз: ");
            }
        }
    }
}

