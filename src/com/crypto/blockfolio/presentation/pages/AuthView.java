package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.domain.exception.AuthException;
import com.crypto.blockfolio.domain.exception.UserAlreadyAuthException;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Scanner;

public class AuthView implements ViewService {

    private final AuthService authService;
    private final Scanner scanner;

    public AuthView() {
        this.authService = ApplicationContext.getAuthService();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void display() {
        try {
            System.out.println("\n=== Авторизація користувача ===");

            // Введення даних користувача
            System.out.print("Введіть логін: ");
            String username = scanner.nextLine();

            System.out.print("Введіть пароль: ");
            String password = scanner.nextLine();

            // Авторизація користувача
            if (authService.authenticate(username, password)) {
                System.out.println("Авторизація успішна!");
                System.out.println("Вітаємо, " + authService.getUser().getUsername());
                DashBoardView dashBoardView = new DashBoardView();
                dashBoardView.display();
            } else {
                System.err.println("Невірний логін або пароль.");
                redirectToMainMenu();
            }
        } catch (UserAlreadyAuthException e) {
            System.err.println("Помилка: " + e.getMessage());
            redirectToMainMenu();
        } catch (AuthException e) {
            System.err.println("Помилка авторизації: Невірний логін або пароль.");
            redirectToMainMenu();
        } catch (Exception e) {
            System.err.println("Несподівана помилка: " + e.getMessage());
            redirectToMainMenu();
        }
    }

    private void redirectToMainMenu() {
        RedirectView redirectView = new RedirectView();
        redirectView.display();
    }
}
