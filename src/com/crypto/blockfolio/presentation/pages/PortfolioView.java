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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
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
        System.out.println("Оновлення даних криптовалют з API...");
        try {
            // Викликаємо метод для отримання всіх криптовалют
            List<Cryptocurrency> cryptocurrencies = cryptocurrencyService.getAllCryptocurrencies();
            System.out.printf("Успішно оновлено %d криптовалют.%n", cryptocurrencies.size());
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
        updateCryptocurrenciesFromApi();
        System.out.println("Читання даних з файлу...");
        List<Cryptocurrency> cryptocurrencies = readCryptocurrenciesFromFile();

        User currentUser = authService.getUser();

        while (true) {
            List<Portfolio> portfolios = loadUserPortfolios(currentUser);

            System.out.println("\n=== Портфелі користувача ===");
            System.out.println("[0] Повернутися назад");
            System.out.println("[+] Створити новий портфель");

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
        }
    }

    private List<Cryptocurrency> readCryptocurrenciesFromFile() {
        return new ArrayList<>(cryptocurrencyRepository.findAll());
    }

    private List<Portfolio> loadUserPortfolios(User user) {
        return user.getPortfolios().stream()
            .map(portfolioId -> {
                try {
                    return portfolioService.getPortfolioById(portfolioId);
                } catch (Exception e) {
                    System.out.printf("Помилка завантаження портфеля з ID %s: %s%n", portfolioId,
                        e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .peek(portfolio -> {
                try {
                    portfolioService.calculateTotalValue(portfolio.getId());
                } catch (Exception e) {
                    System.out.printf("Помилка підрахунку вартості портфеля %s: %s%n",
                        portfolio.getName(), e.getMessage());
                }
            })
            .sorted(Comparator.comparing(p -> p.getTotalValue().doubleValue(),
                Comparator.reverseOrder()))
            .collect(Collectors.toList());
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

    private void viewPortfolioDetails(Portfolio portfolio) {
        while (true) {
            System.out.printf("\n=== Портфель: %s ===\n", portfolio.getName());
            System.out.printf("Вартість портфеля: %.2f$\n", portfolio.getTotalValue());
            System.out.printf("Кількість монет у портфелі: %d\n", portfolio.getBalances().size());

            System.out.println("\nМонети в портфелі:");
            if (portfolio.getBalances().isEmpty()) {
                System.out.println("У портфелі немає монет.");
            } else {
                portfolio.getBalances().forEach((symbol, balance) -> {
                    Optional<Cryptocurrency> cryptoOpt = cryptocurrencyRepository.findBySymbol(
                        symbol);
                    if (cryptoOpt.isPresent()) {
                        Cryptocurrency crypto = cryptoOpt.get();
                        System.out.printf(
                            "Назва: %s | Символ: %s | Баланс: %.2f | Поточна ціна: %.2f | Загальна вартість: %.2f%n",
                            crypto.getName(), symbol, balance, crypto.getCurrentPrice(),
                            crypto.getCurrentPrice() * balance.doubleValue());
                    } else {
                        System.out.printf("Помилка: Дані про криптовалюту %s відсутні.%n", symbol);
                    }
                });
            }

            System.out.println("\n[0] Повернутися назад");
            System.out.println("[+] Додати монету");
            System.out.println("[-] Видалити монету");
            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                break;
            } else if (input.equals("+")) {
                addCryptocurrencyToPortfolio(portfolio);
            } else if (input.equals("-")) {
                removeCryptocurrencyFromPortfolio(portfolio);
            } else {
                System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private void addCryptocurrencyToPortfolio(Portfolio portfolio) {
        System.out.print("Введіть символ криптовалюти: ");
        String symbol = scanner.nextLine().trim();

        System.out.print("Введіть кількість: ");
        try {
            BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Помилка: Кількість повинна бути більше нуля.");
                return;
            }

            Optional<Cryptocurrency> cryptoOpt = cryptocurrencyRepository.findBySymbol(symbol);
            if (cryptoOpt.isPresent()) {
                Cryptocurrency crypto = cryptoOpt.get();
                portfolio.getBalances().put(symbol,
                    portfolio.getBalances().getOrDefault(symbol, BigDecimal.ZERO).add(amount));
                portfolioService.calculateTotalValue(portfolio.getId());
                System.out.printf("Криптовалюта %s успішно додана до портфеля %s.%n",
                    crypto.getName(),
                    portfolio.getName());
            } else {
                System.out.printf("Помилка: Дані про криптовалюту %s відсутні.%n", symbol);
            }
        } catch (NumberFormatException e) {
            System.out.println("Помилка: Некоректна кількість.");
        }
    }

    private void removeCryptocurrencyFromPortfolio(Portfolio portfolio) {
        System.out.print("Введіть символ криптовалюти для видалення: ");
        String symbol = scanner.nextLine().trim();

        if (!portfolio.getBalances().containsKey(symbol)) {
            System.out.println("Помилка: Криптовалюта відсутня у портфелі.");
            return;
        }

        portfolio.getBalances().remove(symbol);
        portfolioService.calculateTotalValue(portfolio.getId());
        System.out.printf("Криптовалюта %s успішно видалена з портфеля %s.%n", symbol,
            portfolio.getName());
    }
}
