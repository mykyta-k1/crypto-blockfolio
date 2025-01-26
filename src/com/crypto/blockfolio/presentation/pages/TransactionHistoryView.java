package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.TransactionService;
import com.crypto.blockfolio.domain.dto.TransactionAddDto;
import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.persistence.entity.TransactionType;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class TransactionHistoryView implements ViewService {

    private final TransactionService transactionService;
    private final UUID portfolioId;
    private final Scanner scanner;

    public TransactionHistoryView(UUID portfolioId) {
        this.transactionService = ApplicationContext.getTransactionService();
        this.portfolioId = portfolioId;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void display() {
        while (true) {
            List<Transaction> transactions = transactionService.getTransactionsByPortfolioId(
                portfolioId);

            System.out.println("\n=== Історія транзакцій ===");
            if (transactions.isEmpty()) {
                System.out.println("Транзакцій немає.");
                break;
            }

            // Виведення списку транзакцій
            for (int i = 0; i < transactions.size(); i++) {
                Transaction transaction = transactions.get(i);
                System.out.printf(
                    "[%d] Тип: %s | Монета: %s | Кількість: %.2f | Витрати: %.2f | Прибуток: %.2f | Комісія: %.2f | Опис: %s | Час: %s%n",
                    i + 1, transaction.getTransactionType(),
                    transaction.getCryptocurrency().getSymbol(),
                    transaction.getAmount(), transaction.getCosts(),
                    transaction.getProfit() != null ? transaction.getProfit() : 0.0,
                    transaction.getFees(),
                    transaction.getDescription() != null ? transaction.getDescription()
                        : "Без опису",
                    transaction.getCreatedAt().toString() // Додано час створення
                );
            }

            System.out.println("\n[0] Повернутися назад");
            System.out.println("[e] Редагувати транзакцію");
            System.out.println("[-] Видалити транзакцію");
            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "0" -> {
                    return; // Повернення до попереднього меню
                }
                case "e" -> {
                    System.out.print("Введіть номер транзакції для редагування: ");
                    try {
                        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                        if (index >= 0 && index < transactions.size()) {
                            Transaction selectedTransaction = transactions.get(index);
                            editTransaction(selectedTransaction);
                        } else {
                            System.out.println("Невірний номер транзакції. Спробуйте ще раз.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Помилка: Введіть правильний номер.");
                    }
                }
                case "-" -> removeTransaction(transactions);
                default -> System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }


    private void editTransaction(Transaction transaction) {
        UUID transactionId = transaction.getId();
        String selectedCrypto = transaction.getCryptocurrency().getSymbol();
        TransactionType transactionType = transaction.getTransactionType();
        BigDecimal amount = transaction.getAmount();
        BigDecimal costs = transaction.getCosts();
        BigDecimal fees = transaction.getFees();
        String description = transaction.getDescription();

        while (true) {
            System.out.println("\n=== Редагування транзакції ===");
            System.out.println("[1] Криптовалюта: " + selectedCrypto);
            System.out.println("[2] Тип транзакції: " + transactionType);
            System.out.println("[3] Кількість: " + amount);
            System.out.println("[4] Витрати: " + costs);
            System.out.println("[5] Комісія: " + fees);
            System.out.println("[6] Опис: " + (description != null ? description : "Не вказано"));
            System.out.println("[7] Підтвердити зміни");
            System.out.println("[0] Відмінити зміни");
            System.out.print("Ваш вибір: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "0" -> {
                    System.out.println("Редагування скасовано.");
                    return;
                }
                case "1" -> {
                    System.out.print("Введіть новий символ криптовалюти: ");
                    selectedCrypto = scanner.nextLine().trim().toUpperCase();
                    // Перевірка чи криптовалюта є в портфелі
                    if (!portfolioContainsCryptocurrency(selectedCrypto)) {
                        System.out.println(
                            "Помилка: Криптовалюта " + selectedCrypto + " не додана до портфеля.");
                        return;
                    }
                }
                case "2" -> {
                    System.out.print("Введіть новий тип транзакції (BUY/SELL): ");
                    transactionType = TransactionType.valueOf(
                        scanner.nextLine().trim().toUpperCase());
                }
                case "3" -> {
                    System.out.print("Введіть нову кількість: ");
                    amount = new BigDecimal(scanner.nextLine().trim());
                }
                case "4" -> {
                    System.out.print("Введіть нові витрати: ");
                    costs = new BigDecimal(scanner.nextLine().trim());
                }
                case "5" -> {
                    System.out.print("Введіть нову комісію: ");
                    fees = new BigDecimal(scanner.nextLine().trim());
                }
                case "6" -> {
                    System.out.print("Введіть новий опис: ");
                    description = scanner.nextLine().trim();
                }
                case "7" -> {
                    // Використання TransactionAddDto для оновлення
                    try {
                        TransactionAddDto transactionAddDto = new TransactionAddDto(
                            transactionId,            // ID транзакції
                            portfolioId,              // ID портфеля
                            selectedCrypto,           // Символ криптовалюти
                            transactionType,          // Тип транзакції
                            amount,                   // Кількість
                            costs,                    // Витрати
                            fees,                     // Комісія
                            description               // Опис
                        );

                        // Оновлення транзакції через сервіс
                        transactionService.updateTransaction(transactionId, transactionAddDto);
                        System.out.println("Транзакцію успішно оновлено!");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Помилка: " + e.getMessage());
                    }
                    return;
                }

                default -> System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private boolean portfolioContainsCryptocurrency(String cryptocurrencySymbol) {
        return ApplicationContext.getPortfolioService()
            .getPortfolioById(portfolioId)
            .getBalances()
            .containsKey(cryptocurrencySymbol);
    }


    private void removeTransaction(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("Транзакцій немає для видалення.");
            return;
        }

        System.out.print("Введіть номер транзакції для видалення: ");
        try {
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (index >= 0 && index < transactions.size()) {
                Transaction transaction = transactions.get(index);
                transactionService.deleteTransaction(transaction.getId());
                System.out.printf("Транзакцію з ID %s успішно видалено.%n", transaction.getId());
            } else {
                System.out.println("Невірний номер транзакції. Спробуйте ще раз.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Помилка: Введіть правильний номер.");
        }
    }
}
