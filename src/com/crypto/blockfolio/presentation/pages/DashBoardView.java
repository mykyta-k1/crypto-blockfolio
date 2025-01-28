package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Scanner;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * Клас {@code DashBoardView} представляє головне меню програми, яке забезпечує навігацію між
 * основними функціями системи.
 */
public class DashBoardView implements ViewService {

    private final Scanner scanner;
    private final AuthService authService;

    public DashBoardView() {
        this.scanner = new Scanner(System.in);
        this.authService = ApplicationContext.getAuthService();
    }

    /**
     * Відображає головне меню програми та забезпечує навігацію до відповідних підменю. Перевіряє,
     * чи користувач автентифікований, та перенаправляє до авторизації, якщо ні.
     *
     * @throws NotImplementedException якщо викликається недоступна функція.
     */
    @Override
    public void display() throws NotImplementedException {

        if (!authService.isAuthenticated()) {
            RedirectView redirectView = new RedirectView();
            redirectView.display();
            return;
        }

        while (true) {
            System.out.println("\n🌟 ГОЛОВНЕ МЕНЮ 🌟");
            System.out.println("━━━━━━━━━━━━━━━━━━");
            System.out.println("1. 💰 Криптовалюти");
            System.out.println("2. 📊 Портфоліо");
            System.out.println("3. 👤 Особистий кабінет");
            System.out.println("0. 🚪 Завершити роботу");
            System.out.println("━━━━━━━━━━━━━━━━━━");
            System.out.print("⭐ Оберіть опцію: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> {
                    CryptocurrenciesView cryptocurrenciesView = new CryptocurrenciesView();
                    cryptocurrenciesView.display();
                }
                case "2" -> {
                    PortfolioView portfolioView = new PortfolioView();
                    portfolioView.display();
                }
                case "3" -> {
                    AccountView accountView = new AccountView();
                    accountView.display();
                }
                case "0" -> {
                    System.out.println("\n👋 Дякуємо, що користуєтесь Blockfolio!");
                    System.exit(0);
                }
                default ->
                    System.err.println("❌ Невірний вибір! Будь ласка, оберіть число від 0 до 3.");
            }
        }
    }
}
