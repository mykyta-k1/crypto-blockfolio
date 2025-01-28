package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.dto.TransactionAddDto;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.entity.TransactionType;
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

    /**
     * Відображає інтерфейс для створення нової транзакції. Користувач може вибрати криптовалюту,
     * тип транзакції, ввести кількість, витрати, комісію та додати опис перед підтвердженням
     * транзакції.
     */
    public void display() {
        String selectedCrypto = null;
        TransactionType transactionType = null;
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal costs = BigDecimal.ZERO;
        BigDecimal fees = BigDecimal.ZERO;
        String description = null;

        while (true) {
            System.out.println("\n💱 СТВОРЕННЯ НОВОЇ ТРАНЗАКЦІЇ");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("1. 🪙 Криптовалюта: %s%n",
                (selectedCrypto != null ? selectedCrypto : "❌ Не вибрано"));
            System.out.printf("2. 📝 Тип операції: %s%n",
                (transactionType != null ? transactionType : "❌ Не вибрано"));
            System.out.printf("3. 💎 Кількість: %s%n", amount);
            System.out.printf("4. 💰 Сума: %s USD%n", costs);
            System.out.printf("5. 💸 Комісія: %s USD%n", fees);
            System.out.printf("6. 📝 Коментар: %s%n",
                (description != null ? description : "❌ Не вказано"));
            System.out.println("7. ✅ Підтвердити транзакцію");
            System.out.println("0. 🔙 Повернутися");
            System.out.print("✨ Оберіть опцію: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "0":
                    return;
                case "1":
                    selectedCrypto = selectCryptocurrency();
                    break;
                case "2":
                    transactionType = selectTransactionType();
                    break;
                case "3":
                    amount = inputBigDecimal("💎 Введіть кількість: ");
                    break;
                case "4":
                    costs = inputBigDecimal("💰 Введіть суму (USD): ");
                    break;
                case "5":
                    fees = inputBigDecimal("💸 Введіть комісію (USD): ");
                    break;
                case "6":
                    System.out.print("📝 Додайте коментар: ");
                    description = scanner.nextLine().trim();
                    break;
                case "7":
                    if (selectedCrypto == null || transactionType == null
                        || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        System.out.println("⚠️ Заповніть усі обов'язкові поля");
                    } else {
                        createTransaction(selectedCrypto, transactionType, amount, costs, fees,
                            description);
                        return;
                    }
                    break;
                default:
                    System.out.print("⚠️ Некоректний вибір. Спробуйте ще раз: ");
            }
        }
    }

    private String selectCryptocurrency() {
        System.out.println("\n🪙 ОБЕРІТЬ КРИПТОВАЛЮТУ");
        System.out.println("━━━━━━━━━━━━━━━━━━");
        List<String> cryptos = new ArrayList<>(portfolio.getBalances().keySet());
        for (int i = 0; i < cryptos.size(); i++) {
            System.out.printf("%d. 💎 %s%n", i + 1, cryptos.get(i));
        }
        int choice = inputInt("✨ Ваш вибір: ");
        if (choice > 0 && choice <= cryptos.size()) {
            return cryptos.get(choice - 1);
        } else {
            System.out.print("⚠️ Некоректний вибір. Спробуйте ще раз: ");
            return null;
        }
    }

    private TransactionType selectTransactionType() {
        System.out.println("\n📝 ТИП ТРАНЗАКЦІЇ");
        System.out.println("━━━━━━━━━━━━━");
        System.out.println("1. 📥 Купівля");
        System.out.println("2. 📤 Продаж");
        System.out.println("3. ⬇️ Поповнення");
        System.out.println("4. ⬆️ Виведення");
        int choice = inputInt("✨ Ваш вибір: ");
        return switch (choice) {
            case 1 -> TransactionType.BUY;
            case 2 -> TransactionType.SELL;
            case 3 -> TransactionType.TRANSFER_DEPOSIT;
            case 4 -> TransactionType.TRANSFER_WITHDRAWAL;
            default -> {
                System.out.print("⚠️ Некоректний вибір (1-4). Спробуйте ще раз: ");
                yield null;
            }
        };
    }

    private void createTransaction(String crypto, TransactionType type, BigDecimal amount,
        BigDecimal costs, BigDecimal fees, String description) {
        BigDecimal currentBalance = portfolio.getBalances().getOrDefault(crypto, BigDecimal.ZERO);

        switch (type) {
            case BUY, TRANSFER_DEPOSIT -> {
                portfolio.getBalances().put(crypto, currentBalance.add(amount));
                System.out.printf("✅ Баланс %s збільшено на %.4f%n", crypto, amount);
            }
            case SELL, TRANSFER_WITHDRAWAL -> {
                if (currentBalance.compareTo(amount) < 0) {
                    System.out.println("⚠️ Недостатньо коштів для проведення операції");
                    return;
                }
                portfolio.getBalances().put(crypto, currentBalance.subtract(amount));
                System.out.printf("✅ Баланс %s зменшено на %.4f%n", crypto, amount);
            }
        }

        TransactionAddDto transactionAddDto = new TransactionAddDto(
            UUID.randomUUID(),  // Унікальний ID транзакції
            portfolio.getId(),  // ID портфеля
            crypto,             // Символ криптовалюти
            type,               // Тип транзакції
            amount,             // Кількість
            costs,              // Витрати
            fees,               // Комісія
            description         // Опис
        );

        // Оновлення загальної вартості портфеля
        ApplicationContext.getPortfolioService().calculateTotalValue(portfolio);
        ApplicationContext.getTransactionService().addTransaction(transactionAddDto);
        System.out.println("✅ Транзакцію успішно створено!");
    }

    private BigDecimal inputBigDecimal(String prompt) {
        System.out.print(prompt + " ");
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("⚠️ Некоректне значення. Спробуйте ще раз: ");
            }
        }
    }

    private int inputInt(String prompt) {
        System.out.print(prompt + " ");
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("⚠️ Некоректне значення. Спробуйте ще раз: ");
            }
        }
    }
}

