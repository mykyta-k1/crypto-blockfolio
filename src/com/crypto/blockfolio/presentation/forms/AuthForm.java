/*
package com.crypto.blockfolio.presentation.forms;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.domain.exception.AuthException;
import com.crypto.blockfolio.domain.exception.UserAlreadyAuthException;
import com.crypto.blockfolio.presentation.pages.DashBoardView;
import java.util.Scanner;

public class AuthForm {

    private final AuthService authService;

    public AuthForm(AuthService authService) {
        this.authService = authService;
    }

    public void submit() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Введення даних користувача
            System.out.print("Введіть логін: ");
            String username = scanner.nextLine();

            System.out.print("Введіть пароль: ");
            String password = scanner.nextLine();

            // Авторизація користувача
            boolean isAuthenticated = authService.authenticate(username, password);

            if (isAuthenticated) {
                System.out.println("Авторизація успішна!");
                System.out.println("Вітаємо, " + authService.getUser().getUsername());
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

 */
