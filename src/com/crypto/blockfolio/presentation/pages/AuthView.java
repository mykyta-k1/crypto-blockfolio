package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.exception.AuthException;
import com.crypto.blockfolio.domain.exception.UserAlreadyAuthException;
import com.crypto.blockfolio.presentation.Main;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Scanner;

public class AuthView implements ViewService {

    @Override
    public void display() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=== Авторизація користувача ===");

            // Введення даних користувача
            System.out.print("Введіть логін: ");
            String username = scanner.nextLine();

            System.out.print("Введіть пароль: ");
            String password = scanner.nextLine();

            // Авторизація користувача
            boolean isAuthenticated = Main.getAuthService().authenticate(username, password);

            if (isAuthenticated) {
                System.out.println("Авторизація успішна!");
                System.out.println("Вітаємо, " + Main.getAuthService().getUser().getUsername());
                DashBoardView dashBoardView = new DashBoardView();
                dashBoardView.display();
            } else {
                System.out.println("Невірний логін або пароль.");
            }

        } catch (UserAlreadyAuthException e) {
            System.err.println("Помилка: Ви вже авторизовані як " + e.getMessage());
        } catch (AuthException e) {
            System.err.println("Помилка авторизації: Невірний логін або пароль.");
        } catch (Exception e) {
            System.err.println("Сталася помилка: " + e.getMessage());
        }
    }
}
