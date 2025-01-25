package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Scanner;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class DashBoardView implements ViewService {

    private final Scanner scanner;
    private final AuthService authService;

    public DashBoardView() {
        this.scanner = new Scanner(System.in);
        this.authService = ApplicationContext.getAuthService();
    }

    @Override
    public void display() throws NotImplementedException {
        // Check if the user is authenticated
        if (!authService.isAuthenticated()) {
            System.out.println(
                "Користувач не авторизований. Переадресація на сторінку вибору дій.");
            RedirectView redirectView = new RedirectView();
            redirectView.display();
            return;
        }

        while (true) {
            System.out.println("\n=== Головне меню ===");
            System.out.println("[1] Криптовалюти");
            System.out.println("[2] Портфоліо");
            System.out.println("[3] Акаунт");
            System.out.println("[4] Про програму");
            System.out.println("[5] Вийти");
            System.out.print("Оберіть дію: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    CryptocurrenciesView cryptocurrenciesView = new CryptocurrenciesView();
                    cryptocurrenciesView.display();
                }
                case "2" -> {
                    PortfolioView portfolioView = new PortfolioView();
                    portfolioView.display();
                    System.out.println("Портфоліо сторінка");
                }
                case "3" -> {
                    AccountView accountView = new AccountView();
                    accountView.display();
                }
                case "4" -> System.out.println(
                    "Blockfolio - Особистий криптопортфель для управління інвестиціями.");
                case "5" -> {
                    System.out.println("Дякуємо за використання Blockfolio! До побачення.");
                    System.exit(0);
                }
                default -> System.err.println("Помилка: Невірний вибір. Спробуйте ще раз.");
            }
        }
    }
}
