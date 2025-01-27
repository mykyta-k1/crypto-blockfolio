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
            // Викликаємо метод для отримання всіх криптовалют
            List<Cryptocurrency> cryptocurrencies = cryptocurrencyService.getAllCryptocurrencies();
            //System.out.printf("Успішно оновлено %d криптовалют.%n", cryptocurrencies.size());
        } catch (Exception e) {
            System.err.printf("Помилка оновлення криптовалют з API: %s%n", e.getMessage());
        }
    }


    @Override
    public void display() {
        if (!authService.isAuthenticated()) {
            System.out.println("Користувач не авторизований. Повернення до головного меню...");
            return;
        }
        // Завантаження криптовалют асинхронно
        CompletableFuture<Void> asyncUpdate = CompletableFuture.runAsync(
            this::updateCryptocurrenciesFromApi);
        // Показуємо меню одразу, без очікування завершення запиту
        showMenu(asyncUpdate);
    }

    private void showMenu(CompletableFuture<Void> asyncUpdate) {
        while (true) {
            System.out.println("\n=== Портфелі користувача ===");
            System.out.println("[0] Повернутися назад");
            System.out.println("[+] Створити новий портфель");

            User currentUser = authService.getUser();
            List<Portfolio> portfolios = loadUserPortfolios(currentUser);

            if (portfolios.isEmpty()) {
                System.out.println("У вас немає створених портфелів.");
            } else {
                for (int i = 0; i < portfolios.size(); i++) {
                    Portfolio portfolio = portfolios.get(i);
                    System.out.printf("[%d] %s [Вартість: %.2f$]%n", i + 1, portfolio.getName(),
                        portfolio.getTotalValue());
                }
            }

            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                System.out.println("Повернення до головного меню...");
                return;
            } else if (input.equals("+")) {
                createPortfolio(currentUser);
            } else {
                try {
                    int selectedIndex = Integer.parseInt(input) - 1;
                    if (selectedIndex >= 0 && selectedIndex < portfolios.size()) {
                        viewPortfolioDetails(portfolios.get(selectedIndex));
                    } else {
                        System.out.println("Помилка: Невірний вибір. Спробуйте ще раз.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Помилка: Введіть номер або '+'.");
                }
            }

            // Перевірка завершення асинхронного завантаження
            if (asyncUpdate.isDone()) {
                System.out.println("Дані криптовалют оновлено.");
            }
        }
    }

    private List<Cryptocurrency> readCryptocurrenciesFromFile() {
        return new ArrayList<>(cryptocurrencyRepository.findAll());
    }

    private List<Portfolio> loadUserPortfolios(User user) {
        List<UUID> validPortfolioIds = new ArrayList<>();
        List<Portfolio> portfolios = user.getPortfolios().stream()
            .map(portfolioId -> {
                try {
                    Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
                    validPortfolioIds.add(portfolioId);
                    return portfolio;
                } catch (Exception e) {
                    System.out.printf("Помилка завантаження портфеля з ID %s: %s%n", portfolioId,
                        e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .peek(portfolio -> {
                try {
                    portfolioService.calculateTotalValue(portfolio);
                } catch (Exception e) {
                    System.out.printf("Помилка підрахунку вартості портфеля %s: %s%n",
                        portfolio.getName(), e.getMessage());
                }
            })
            .sorted(Comparator.comparing(p -> p.getTotalValue().doubleValue(),
                Comparator.reverseOrder()))
            .collect(Collectors.toList());

        // Оновлюємо список портфелів користувача
        user.setPortfolios(new HashSet<>(validPortfolioIds));
        authService.updateUser(user);

        return portfolios;
    }


    private void createPortfolio(User currentUser) {
        System.out.print("Введіть назву нового портфеля: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Помилка: Назва портфеля не може бути порожньою.");
            return;
        }

        try {
            // Створення DTO для нового портфеля
            PortfolioAddDto portfolioAddDto = new PortfolioAddDto(
                UUID.randomUUID(),
                currentUser.getId(),
                name,
                Map.of(), // Порожній баланс для нового портфеля
                Set.of()  // Порожній список транзакцій
            );

            // Додавання портфеля до сервісу
            Portfolio newPortfolio = portfolioService.addPortfolio(portfolioAddDto);

            // Додавання ID портфеля до поточного користувача
            currentUser.getPortfolios().add(newPortfolio.getId());

            // Збереження оновленого користувача
            authService.updateUser(currentUser);

            System.out.printf("Портфель '%s' успішно створено.%n", newPortfolio.getName());
        } catch (IllegalArgumentException e) {
            // Вивід помилок у форматі списку
            System.out.println("\nНекоректні дані портфеля:");
            String[] errorMessages = e.getMessage()
                .replace("[", "")
                .replace("]", "")
                .split(","); // Розділення помилок на окремі рядки
            for (String error : errorMessages) {
                System.out.printf("- %s%n", error.trim());
            }
        } catch (Exception e) {
            System.out.printf("Неочікувана помилка: %s%n", e.getMessage());
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
            // Розрахунок зміни портфеля за останні 24 години
            AtomicReference<BigDecimal> totalValueChange24h = new AtomicReference<>(
                BigDecimal.ZERO);

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

            // Загальна зміна у відсотках
            BigDecimal totalPortfolioValue = portfolio.getTotalValue();
            BigDecimal percentChange24h = totalPortfolioValue.compareTo(BigDecimal.ZERO) > 0
                ? totalValueChange24h.get()
                .divide(totalPortfolioValue, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

            BigDecimal totalPnl = portfolio.calculateTotalPnl();
            // Виведення інформації про портфель
            System.out.printf("\n=== Портфель: %s ===\n", portfolio.getName());
            System.out.printf("Вартість портфеля: %.2f$\n", totalPortfolioValue);
            System.out.printf("Зміна за 24 години: %.2f$ (%.2f%%)\n",
                totalValueChange24h.get(),
                percentChange24h);
            System.out.printf("Загальний PNL: %.2f$\n", totalPnl);
            System.out.printf("Кількість монет у портфелі: %d\n", portfolio.getBalances().size());

            // Шапка таблиці
            System.out.printf("%-5s %-15s %-10s %-15s %-10s %-20s %-25s %-10s %-10s%n",
                "#", "Монета", "Символ", "Ціна (USD)", "Баланс", "Обсяг 24г (USD)",
                "Ринкова капіталізація", "Зміна 24г (%)", "PNL (USD)");

            if (portfolio.getBalances().isEmpty()) {
                System.out.println("У портфелі немає монет.");
            } else {
                int index = 1;
                for (Map.Entry<String, BigDecimal> entry : portfolio.getBalances().entrySet()) {
                    String symbol = entry.getKey();
                    BigDecimal balance = entry.getValue();

                    Optional<Cryptocurrency> cryptoOpt = findCryptocurrency(symbol);
                    if (cryptoOpt.isPresent()) {
                        Cryptocurrency crypto = cryptoOpt.get();

                        // Розрахунок PNL
                        BigDecimal pnl = portfolio.calculatePnlForCryptocurrency(symbol);

                        // Форматування даних
                        System.out.printf(
                            "%-5d %-15s %-10s %-15.2f %-10.4f %-20.2f %-25.2f %-10.2f %-10.2f%n",
                            index++, crypto.getName(), crypto.getSymbol(), crypto.getCurrentPrice(),
                            balance,
                            crypto.getVolume24h(), crypto.getMarketCap(),
                            crypto.getPercentChange24h(), pnl);
                    } else {
                        System.out.printf("%-5d %-15s %-10s Дані недоступні%n", index++, symbol,
                            symbol);
                    }
                }
            }

            // Меню дій з портфелем
            System.out.println("\n[0] Повернутися назад");
            System.out.println("[t] Створити транзакцію");
            System.out.println("[h] Історія транзакцій");
            System.out.println("[+] Додати монету");
            System.out.println("[-] Видалити монету");
            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "0" -> {
                    return; // Повернення до попереднього меню
                }
                case "t" -> {
                    TransactionsView transactionsView = new TransactionsView(portfolio.getId());
                    transactionsView.display(); // Виклик сторінки транзакцій
                }
                case "h" -> {
                    TransactionHistoryView transactionHistoryView = new TransactionHistoryView(
                        portfolio.getId());
                    transactionHistoryView.display();
                }
                case "+" -> addCryptocurrencyToPortfolio(portfolio);
                case "-" -> removeCryptocurrencyFromPortfolio(portfolio);
                default -> System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private void addCryptocurrencyToPortfolio(Portfolio portfolio) {
        System.out.print("Введіть символ або назву криптовалюти: ");
        String input = scanner.nextLine().trim()
            .toUpperCase(); // Перетворюємо символ у верхній регістр

        Optional<Cryptocurrency> cryptoOpt = findCryptocurrency(input);
        if (cryptoOpt.isPresent()) {
            Cryptocurrency crypto = cryptoOpt.get();
            String normalizedSymbol = crypto.getSymbol().toUpperCase(); // Нормалізуємо символ

            // Перевірка, чи криптовалюта вже є в портфелі
            if (portfolio.getBalances().containsKey(normalizedSymbol)) {
                System.out.println("Криптовалюта вже є у портфелі.");
                return;
            }

            // Додаємо криптовалюту з початковим балансом 0
            portfolio.getBalances().put(normalizedSymbol, BigDecimal.ZERO);
            portfolioService.calculateTotalValue(portfolio);
            System.out.printf(
                "Криптовалюта %s успішно додана до портфеля %s",
                crypto.getName(), portfolio.getName());
        } else {
            System.out.printf("Помилка: Дані про криптовалюту %s відсутні.%n", input);
        }
    }

    private Optional<Cryptocurrency> findCryptocurrency(String input) {
        // Перетворюємо введений текст у верхній регістр для пошуку
        String normalizedInput = input.toUpperCase();

        // Спочатку шукаємо за символом
        Optional<Cryptocurrency> cryptoOpt = cryptocurrencyRepository.findBySymbol(normalizedInput);
        if (cryptoOpt.isPresent()) {
            return cryptoOpt;
        }

        // Якщо не знайдено за символом, шукаємо за назвою
        return cryptocurrencyRepository.findByName(input);
    }


    private void removeCryptocurrencyFromPortfolio(Portfolio portfolio) {
        System.out.print("Введіть символ або назву криптовалюти для видалення: ");
        String input = scanner.nextLine().trim().toUpperCase(); // Перетворюємо у верхній регістр

        Optional<Cryptocurrency> cryptoOpt = findCryptocurrency(input);
        if (cryptoOpt.isEmpty()) {
            System.out.printf("Помилка: Дані про криптовалюту %s відсутні.%n", input);
            return;
        }

        Cryptocurrency crypto = cryptoOpt.get();
        String normalizedSymbol = crypto.getSymbol().toUpperCase(); // Нормалізуємо символ
        if (!portfolio.getBalances().containsKey(normalizedSymbol)) {
            System.out.println("Помилка: Криптовалюта відсутня у портфелі.");
            return;
        }

        portfolio.getBalances().remove(normalizedSymbol);
        portfolioService.calculateTotalValue(portfolio);
        System.out.printf("Криптовалюта %s успішно видалена з портфеля %s.%n", crypto.getName(),
            portfolio.getName());
    }

}
