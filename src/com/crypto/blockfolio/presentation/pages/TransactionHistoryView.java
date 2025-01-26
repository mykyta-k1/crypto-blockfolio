package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.TransactionService;
import com.crypto.blockfolio.persistence.entity.Transaction;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
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
            } else {
                for (int i = 0; i < transactions.size(); i++) {
                    Transaction transaction = transactions.get(i);
                    System.out.printf(
                        "[%d] Тип: %s | Монета: %s | Кількість: %.2f | Витрати: %.2f | Прибуток: %.2f | Комісія: %.2f | Опис: %s%n",
                        i + 1, transaction.getTransactionType(),
                        transaction.getCryptocurrency().getSymbol(),
                        transaction.getAmount(), transaction.getCosts(),
                        transaction.getProfit() != null ? transaction.getProfit() : 0.0,
                        transaction.getFees(),
                        transaction.getDescription() != null ? transaction.getDescription()
                            : "Без опису");
                }
            }

            System.out.println("\n[0] Повернутися назад");
            System.out.println("[-] Видалити транзакцію");
            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return; // Повернення до попереднього меню
            } else if (input.equals("-")) {
                removeTransaction(transactions);
            } else {
                System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
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
