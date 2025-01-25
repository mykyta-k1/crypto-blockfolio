package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.domain.contract.PortfolioService;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.presentation.Main;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PortfolioView implements ViewService {

    private final AuthService authService;
    private final PortfolioService portfolioService;
    private final Scanner scanner;

    public PortfolioView() {
        this.authService = Main.getAuthService();
        this.portfolioService = Main.getPortfolioService();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void display() {
        while (true) {
            Set<UUID> portfolioIds = authService.getUser().getPortfolios();
            List<Portfolio> portfolios = portfolioIds.stream()
                .map(portfolioService::getById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(Portfolio::getTotalValue).reversed())
                .collect(Collectors.toList());

            System.out.println("\n=== Портфелі користувача ===");
            if (portfolios.isEmpty()) {
                System.out.println("У вас немає створених портфелів.");
            } else {
                for (int i = 0; i < portfolios.size(); i++) {
                    Portfolio portfolio = portfolios.get(i);
                    System.out.printf("[%d] %s - Загальна вартість: %.2f USD%n",
                        i + 1, portfolio.getName(), portfolio.getTotalValue());
                }
            }

            System.out.println("\n[1] Створити новий портфель");
            System.out.println("[2] Видалити портфель");
            System.out.println("[3] Управляти портфелем");
            System.out.println("[0] Назад");
            System.out.print("Оберіть дію: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    createPortfolio();
                    break;
                case "2":
                    deletePortfolio(portfolios);
                    break;
                case "3":
                    managePortfolio(portfolios);
                    break;
                case "0":
                    return;
                default:
                    System.err.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private void createPortfolio() {
        System.out.print("Введіть назву нового портфеля: ");
        String name = scanner.nextLine();

        Portfolio portfolio = portfolioService.createPortfolio(authService.getUser().getId(), name);
        authService.getUser().addPortfolio(portfolio.getId());

        System.out.println("Новий портфель успішно створено.");
    }

    private void deletePortfolio(List<Portfolio> portfolios) {
        if (portfolios.isEmpty()) {
            System.err.println("У вас немає портфелів для видалення.");
            return;
        }

        System.out.print("Введіть номер портфеля для видалення: ");
        int portfolioIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (portfolioIndex >= 0 && portfolioIndex < portfolios.size()) {
            Portfolio portfolio = portfolios.get(portfolioIndex);
            portfolioService.remove(portfolio);
            authService.getUser().removePortfolio(portfolio.getId());
            System.out.println("Портфель успішно видалено.");
        } else {
            System.err.println("Невірний номер портфеля.");
        }
    }

    private void managePortfolio(List<Portfolio> portfolios) {
        if (portfolios.isEmpty()) {
            System.err.println("У вас немає портфелів для управління.");
            return;
        }

        System.out.print("Введіть номер портфеля для управління: ");
        int portfolioIndex = Integer.parseInt(scanner.nextLine()) - 1;

        if (portfolioIndex >= 0 && portfolioIndex < portfolios.size()) {
            Portfolio portfolio = portfolios.get(portfolioIndex);

            while (true) {
                System.out.println("\n=== Управління портфелем: " + portfolio.getName() + " ===");
                System.out.println("[1] Переглянути транзакції");
                System.out.println("[2] Додати транзакцію");
                System.out.println("[3] Видалити транзакцію");
                System.out.println("[0] Назад");
                System.out.print("Оберіть дію: ");

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        viewTransactions(portfolio);
                        break;
                    case "2":
                        addTransaction(portfolio);
                        break;
                    case "3":
                        deleteTransaction(portfolio);
                        break;
                    case "0":
                        return;
                    default:
                        System.err.println("Невірний вибір. Спробуйте ще раз.");
                }
            }
        } else {
            System.err.println("Невірний номер портфеля.");
        }
    }

    private void viewTransactions(Portfolio portfolio) {
        portfolio.getTransactionsList().forEach(transactionId -> {
            System.out.println("Транзакція ID: " + transactionId);
        });
    }

    private void addTransaction(Portfolio portfolio) {
        System.out.print("Введіть ID транзакції: ");
        UUID transactionId = UUID.fromString(scanner.nextLine());

        boolean added = portfolioService.addTransactionToPortfolio(portfolio.getId(),
            transactionId);
        if (added) {
            System.out.println("Транзакція успішно додана до портфеля.");
        } else {
            System.err.println("Помилка під час додавання транзакції.");
        }
    }

    private void deleteTransaction(Portfolio portfolio) {
        System.out.print("Введіть ID транзакції для видалення: ");
        UUID transactionId = UUID.fromString(scanner.nextLine());

        boolean removed = portfolioService.removeTransactionFromPortfolio(portfolio.getId(),
            transactionId);
        if (removed) {
            System.out.println("Транзакція успішно видалена.");
        } else {
            System.err.println("Помилка під час видалення транзакції.");
        }
    }
}
