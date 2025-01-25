package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.presentation.Main;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Scanner;

public class DashBoardView implements ViewService {

    private final Scanner scanner;

    public DashBoardView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void display() {
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
                case "1":
                    CryptocurrenciesView cryptocurrenciesView = new CryptocurrenciesView(
                        Main.getCryptocurrencyService());
                    cryptocurrenciesView.display();
                    break;
                case "2":
                    PortfolioView portfolioView = new PortfolioView(Main.getPortfolioService());
                    portfolioView.display();
                    break;
                case "3":
                    AccountView accountView = new AccountView(Main.getAuthService());
                    accountView.display();
                    break;
                case "4":
                    AboutView aboutView = new AboutView();
                    aboutView.display();
                    break;
                case "5":
                    System.out.println("Дякуємо, що скористалися нашим додатком. До побачення!");
                    return;
                default:
                    System.err.println("Помилка: Невірний вибір. Спробуйте ще раз.");
            }
        }
    }
}
