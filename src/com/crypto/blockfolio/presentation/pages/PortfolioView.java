package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.domain.contract.PortfolioService;
import com.crypto.blockfolio.domain.dto.PortfolioAddDto;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PortfolioView implements ViewService {

    private final PortfolioService portfolioService;
    private final AuthService authService;
    private final CryptocurrencyService cryptocurrencyService;
    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final Scanner scanner;

    public PortfolioView() {
        this.portfolioService = ApplicationContext.getPortfolioService();
        this.authService = ApplicationContext.getAuthService();
        this.cryptocurrencyService = ApplicationContext.getCryptocurrencyService();
        this.cryptocurrencyRepository = ApplicationContext.getRepositoryFactory()
            .getCryptocurrencyRepository();
        this.scanner = new Scanner(System.in);
    }

    private void updateCryptocurrenciesFromApi() {
        try {
            List<Cryptocurrency> cryptocurrencies = cryptocurrencyService.getAllCryptocurrencies();
        } catch (Exception e) {
            System.err.println("⚠️ Помилка оновлення даних: " + e.getMessage());
        }
    }

    /**
     * Відображає головне меню управління портфелями. Перевіряє автентифікацію користувача та
     * завантажує портфелі.
     */
    @Override
    public void display() {
        if (!authService.isAuthenticated()) {
            System.out.println("🔒 Необхідна авторизація. Повернення до головного меню...");
            return;
        }
        CompletableFuture<Void> asyncUpdate = CompletableFuture.runAsync(
            this::updateCryptocurrenciesFromApi);
        showMenu(asyncUpdate);
    }

    private void showMenu(CompletableFuture<Void> asyncUpdate) {
        while (true) {
            System.out.println("\n💼 ВАШІ ПОРТФЕЛІ");
            System.out.println("━━━━━━━━━━━━━━━");
            System.out.println("0. 🔙 Повернутися");
            System.out.println("➕ Створити новий портфель");

            User currentUser = authService.getUser();
            List<Portfolio> portfolios = loadUserPortfolios(currentUser);

            if (portfolios.isEmpty()) {
                System.out.println("📭 У вас поки що немає портфелів");
            } else {
                for (int i = 0; i < portfolios.size(); i++) {
                    Portfolio portfolio = portfolios.get(i);
                    System.out.printf("%d. 💼 %s 💰 Вартість: $%.2f%n",
                        i + 1,
                        portfolio.getName(),
                        portfolio.getTotalValue());
                }
            }

            System.out.print("✨ Оберіть опцію: ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("🔄 Повернення до головного меню...");
                return;
            } else if (input.equals("+")) {
                createPortfolio(currentUser);
            } else {
                try {
                    int selectedIndex = Integer.parseInt(input) - 1;
                    if (selectedIndex >= 0 && selectedIndex < portfolios.size()) {
                        viewPortfolioDetails(portfolios.get(selectedIndex));
                    } else {
                        System.out.println("⚠️ Помилка: Невірний вибір. Спробуйте ще раз.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ Помилка: Введіть номер або '+'.");
                }
            }

            if (asyncUpdate.isDone()) {
                System.out.println("🔄 Дані криптовалют оновлено.");
            }
        }
    }

    private List<Cryptocurrency> readCryptocurrenciesFromFile() {
        return new ArrayList<>(cryptocurrencyRepository.findAll());
    }

    /**
     * Завантажує список портфелів для даного користувача, видаляючи некоректні посилання.
     *
     * @param user поточний користувач.
     * @return список портфелів користувача.
     */
    private List<Portfolio> loadUserPortfolios(User user) {
        List<UUID> validPortfolioIds = new ArrayList<>();
        List<Portfolio> portfolios = user.getPortfolios().stream()
            .map(portfolioId -> {
                try {
                    Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
                    validPortfolioIds.add(portfolioId);
                    return portfolio;
                } catch (Exception e) {
                    System.out.printf("⚠️ Помилка завантаження портфеля з ID %s: %s%n", portfolioId,
                        e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .peek(portfolio -> {
                try {
                    portfolioService.calculateTotalValue(portfolio);
                } catch (Exception e) {
                    System.out.printf("⚠️ Помилка підрахунку вартості портфеля %s: %s%n",
                        portfolio.getName(), e.getMessage());
                }
            })
            .sorted(Comparator.comparing(p -> p.getTotalValue().doubleValue(),
                Comparator.reverseOrder()))
            .collect(Collectors.toList());

        user.setPortfolios(new HashSet<>(validPortfolioIds));
        authService.updateUser(user);

        return portfolios;
    }

    /**
     * Створює новий портфель для користувача з унікальною назвою.
     *
     * @param currentUser поточний автентифікований користувач.
     */
    private void createPortfolio(User currentUser) {
        System.out.print("\uD83D\uDCBC Введіть назву нового портфеля: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("⚠️ Помилка: Назва портфеля не може бути порожньою.");
            return;
        }

        try {
            PortfolioAddDto portfolioAddDto = new PortfolioAddDto(
                UUID.randomUUID(),
                currentUser.getId(),
                name,
                Map.of(),
                Set.of()
            );

            Portfolio newPortfolio = portfolioService.addPortfolio(portfolioAddDto);
            currentUser.getPortfolios().add(newPortfolio.getId());
            authService.updateUser(currentUser);

            System.out.printf("\uD83D\uDCBC Портфель '%s' успішно створено.%n",
                newPortfolio.getName());
        } catch (IllegalArgumentException e) {
            System.out.println("\n⚠️ Виявлено помилки:");
            String[] errorMessages = e.getMessage()
                .replace("[", "")
                .replace("]", "")
                .split(",");
            for (String error : errorMessages) {
                System.out.printf("❌ %s%n", error.trim());
            }
        } catch (Exception e) {
            System.out.printf("⚠️ Неочікувана помилка: %s%n", e.getMessage());
        }
    }

    private void calculatePortfolioChange(Portfolio portfolio) {
        AtomicReference<BigDecimal> totalValueChange24h = new AtomicReference<>(BigDecimal.ZERO);

        portfolio.getBalances().forEach((symbol, balance) -> {
            Optional<Cryptocurrency> cryptoOpt = findCryptocurrency(symbol);

            if (cryptoOpt.isPresent()) {
                Cryptocurrency crypto = cryptoOpt.get();

                // Отримання вартості монети
                BigDecimal cryptoValue = BigDecimal.valueOf(crypto.getCurrentPrice())
                    .multiply(balance);

                // Розрахунок зміни за 24 години
                BigDecimal cryptoChange24h = cryptoValue
                    .multiply(BigDecimal.valueOf(crypto.getPercentChange24h()))
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

                // Додавання зміни до загальної зміни
                totalValueChange24h.updateAndGet(v -> v.add(cryptoChange24h));
            }
        });

        // Загальна зміна вартістю та у відсотках
        BigDecimal totalPortfolioValue = portfolio.getTotalValue();
        BigDecimal percentChange24h = totalValueChange24h.get()
            .divide(totalPortfolioValue, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));

        // Виведення результатів
        System.out.printf(
            "Зміна портфеля за 24 години: %.2f$ (%.2f%%)%n",
            totalValueChange24h.get(),
            percentChange24h
        );
    }


    private void viewPortfolioDetails(Portfolio portfolio) {
        while (true) {
            AtomicReference<BigDecimal> totalValueChange24h = new AtomicReference<>(
                BigDecimal.ZERO);

            portfolio.getBalances().forEach((symbol, balance) -> {
                Optional<Cryptocurrency> cryptoOpt = findCryptocurrency(symbol);

                if (cryptoOpt.isPresent()) {
                    Cryptocurrency crypto = cryptoOpt.get();

                    BigDecimal cryptoValue = BigDecimal.valueOf(crypto.getCurrentPrice())
                        .multiply(balance);

                    BigDecimal cryptoChange24h = cryptoValue
                        .multiply(BigDecimal.valueOf(crypto.getPercentChange24h()))
                        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

                    totalValueChange24h.updateAndGet(v -> v.add(cryptoChange24h));
                }
            });

            BigDecimal totalPortfolioValue = portfolio.getTotalValue();
            BigDecimal percentChange24h = totalPortfolioValue.compareTo(BigDecimal.ZERO) > 0
                ? totalValueChange24h.get()
                .divide(totalPortfolioValue, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

            BigDecimal totalPnl = portfolio.calculateTotalPnl();
            System.out.println("\n📊 АНАЛІТИКА ПОРТФЕЛЯ: " + portfolio.getName());
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("💰 Загальна вартість: $%.2f%n", totalPortfolioValue);
            BigDecimal change = percentChange24h;
            String changeEmoji = change.compareTo(BigDecimal.ZERO) > 0 ? "🟢" :
                (change.compareTo(BigDecimal.ZERO) < 0 ? "🔴" : "⚪");
            System.out.printf("%s Зміна (24г): $%.2f (%.2f%%)%n",
                changeEmoji,
                totalValueChange24h.get(),
                percentChange24h);
            System.out.printf("📈 Загальний PNL: $%.2f%n", totalPnl);
            System.out.printf("🪙 Кількість активів: %d%n", portfolio.getBalances().size());
            System.out.println("\n📋 СПИСОК АКТИВІВ");
            System.out.println("━━━━━━━━━━━━━━━");
            System.out.printf("%-4s %-15s %-8s %-12s %-10s %-15s %-15s %-10s %-10s%n",
                "#", "Назва", "Тікер", "Ціна USD", "Кількість", "Об'єм 24г",
                "Капіталізація", "Зміна 24г", "PNL USD");
            System.out.println("━".repeat(100));

            /*
            System.out.printf("\n=== Портфель: %s ===\n", portfolio.getName());
            System.out.printf("Вартість портфеля: %.2f$\n", totalPortfolioValue);
            System.out.printf("Зміна за 24 години: %.2f$ (%.2f%%)\n",
                totalValueChange24h.get(),
                percentChange24h);
            System.out.printf("Загальний PNL: %.2f$\n", totalPnl);
            System.out.printf("Кількість монет у портфелі: %d\n", portfolio.getBalances().size());

            System.out.printf("%-5s %-15s %-10s %-15s %-10s %-20s %-25s %-10s %-10s%n",
                "#", "Монета", "Символ", "Ціна (USD)", "Баланс", "Обсяг 24г (USD)",
                "Ринкова капіталізація", "Зміна 24г (%)", "PNL (USD)");

             */

            if (portfolio.getBalances().isEmpty()) {
                System.out.println("\uD83D\uDCBC Портфель порожній");
            } else {
                int index = 1;
                for (Map.Entry<String, BigDecimal> entry : portfolio.getBalances().entrySet()) {
                    String symbol = entry.getKey();
                    BigDecimal balance = entry.getValue();

                    Optional<Cryptocurrency> cryptoOpt = findCryptocurrency(symbol);
                    if (cryptoOpt.isPresent()) {
                        Cryptocurrency crypto = cryptoOpt.get();

                        BigDecimal pnl = portfolio.calculatePnlForCryptocurrency(symbol);

                        System.out.printf(
                            "%-4d %-15s %-8s $%-11.2f %-10.4f $%-14.2f $%-14.2f %s%-9.2f $%-9.2f%n",
                            index++,
                            crypto.getName(),
                            crypto.getSymbol(),
                            crypto.getCurrentPrice(),
                            balance,
                            crypto.getVolume24h(),
                            crypto.getMarketCap(),
                            (crypto.getPercentChange24h() > 0 ? "+" : ""),
                            crypto.getPercentChange24h(),
                            pnl);
                    } else {
                        System.out.printf("%-4d %-15s %-8s ⚠️ Дані тимчасово недоступні%n",
                            index++, symbol, symbol);
                    }
                }
            }

            System.out.println("\n⚙️ УПРАВЛІННЯ ПОРТФЕЛЕМ");
            System.out.println("━━━━━━━━━━━━━━━━━━━");
            System.out.println("0. 🔙 Повернутися");
            System.out.println("T. 💱 Нова транзакція");
            System.out.println("H. 📜 Історія транзакцій");
            System.out.println("➕ Додати актив");
            System.out.println("➖ Видалити актив");
            System.out.print("✨ Оберіть опцію: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "0" -> {
                    return;
                }
                case "t" -> {
                    TransactionsView transactionsView = new TransactionsView(portfolio.getId());
                    transactionsView.display();
                }
                case "h" -> {
                    TransactionHistoryView transactionHistoryView = new TransactionHistoryView(
                        portfolio.getId());
                    transactionHistoryView.display();
                }
                case "+" -> addCryptocurrencyToPortfolio(portfolio);
                case "-" -> removeCryptocurrencyFromPortfolio(portfolio);
                default -> System.out.println("\uD83D\uDCBC Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    /**
     * Додає актив (криптовалюту) до обраного портфеля.
     *
     * @param portfolio обраний портфель.
     */
    private void addCryptocurrencyToPortfolio(Portfolio portfolio) {
        System.out.print("🔍 Введіть тікер або назву криптовалюти: ");
        String input = scanner.nextLine().trim()
            .toUpperCase();

        Optional<Cryptocurrency> cryptoOpt = findCryptocurrency(input);
        if (cryptoOpt.isPresent()) {
            Cryptocurrency crypto = cryptoOpt.get();
            String normalizedSymbol = crypto.getSymbol().toUpperCase();

            if (portfolio.getBalances().containsKey(normalizedSymbol)) {
                System.out.println("⚠️ Цей актив вже є у портфелі");
                return;
            }

            portfolio.getBalances().put(normalizedSymbol, BigDecimal.ZERO);
            portfolioService.calculateTotalValue(portfolio);
            System.out.printf("✅ %s успішно додано до портфеля '%s'%n",
                crypto.getName(), portfolio.getName());
        } else {
            System.out.printf("❌ Актив %s не знайдено%n", input);
        }
    }

    private Optional<Cryptocurrency> findCryptocurrency(String input) {
        String normalizedInput = input.toUpperCase();
        Optional<Cryptocurrency> cryptoOpt = cryptocurrencyRepository.findBySymbol(normalizedInput);
        if (cryptoOpt.isPresent()) {
            return cryptoOpt;
        }
        return cryptocurrencyRepository.findByName(input);
    }

    /**
     * Видаляє актив (криптовалюту) з обраного портфеля.
     *
     * @param portfolio обраний портфель.
     */
    private void removeCryptocurrencyFromPortfolio(Portfolio portfolio) {
        System.out.print("✨ Введіть символ або назву криптовалюти для видалення: ");
        String input = scanner.nextLine().trim().toUpperCase();

        Optional<Cryptocurrency> cryptoOpt = findCryptocurrency(input);
        if (cryptoOpt.isEmpty()) {
            System.out.printf("\uD83D\uDCBC Помилка: Дані про криптовалюту %s відсутні.%n", input);
            return;
        }

        Cryptocurrency crypto = cryptoOpt.get();
        String normalizedSymbol = crypto.getSymbol().toUpperCase();
        if (!portfolio.getBalances().containsKey(normalizedSymbol)) {
            System.out.println("\uD83D\uDCBC Помилка: Криптовалюта відсутня у портфелі.");
            return;
        }

        portfolio.getBalances().remove(normalizedSymbol);
        portfolioService.calculateTotalValue(portfolio);
        System.out.printf("✅ %s успішно видалено з портфеля '%s'%n",
            crypto.getName(), portfolio.getName());
    }

}
