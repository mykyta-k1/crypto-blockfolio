package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.SignUpService;
import com.crypto.blockfolio.domain.contract.UserService;
import com.crypto.blockfolio.domain.dto.UserAddDto;
import com.crypto.blockfolio.domain.exception.SignUpException;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Scanner;
import java.util.UUID;

public class SignUpView implements ViewService {

    private final SignUpService signUpService;
    private final UserService userService;
    private final Scanner scanner;

    public SignUpView() {
        this.signUpService = ApplicationContext.getSignUpService();
        this.userService = ApplicationContext.getUserService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Відображає процес реєстрації нового користувача. Користувач вводить свої дані (логін, пароль,
     * email), які перевіряються на унікальність. У разі виявлення помилок реєстрація переривається,
     * а користувач отримує повідомлення про помилки. Успішна реєстрація завершується переходом до
     * головного меню.
     */
    @Override
    public void display() {
        try {
            System.out.println("\n✨ РЕЄСТРАЦІЯ НОВОГО КОРИСТУВАЧА");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            // Введення даних користувача
            System.out.print("👤 Логін: ");
            String username = scanner.nextLine();

            System.out.print("🔒 Пароль: ");
            String password = scanner.nextLine();

            System.out.print("📧 Email: ");
            String email = scanner.nextLine();

            if (userService.getAll().stream().anyMatch(user ->
                user.getUsername().equalsIgnoreCase(username) ||
                    user.getEmail().equalsIgnoreCase(email))) {
                System.err.println("❌ Користувач із таким логіном або email вже існує");
                redirectToMainMenu();
                return;
            }

            UserAddDto userAddDto = new UserAddDto(UUID.randomUUID(), username, password, email,
                null);

            System.out.println("📨 Код підтвердження надіслано на вашу пошту");
            signUpService.signUp(userAddDto, () -> {
                System.out.print("🔑 Введіть код підтвердження: ");
                return scanner.nextLine();
            });

            System.out.println("✅ Реєстрація успішна! Ласкаво просимо!");
            DashBoardView dashBoardView = new DashBoardView();
            dashBoardView.display();
        } catch (SignUpException e) {
            System.err.println("❌ Помилка реєстрації: " + e.getMessage());
            redirectToMainMenu();
        } catch (EntityArgumentException e) {
            System.err.println("\n⚠️ Виявлено помилки в даних:");
            e.getErrors().forEach(error -> System.err.println("- " + error));
            redirectToMainMenu();
        } catch (Exception e) {
            System.err.println("❌ Помилка реєстрації: " + e.getMessage());
            redirectToMainMenu();
        }
    }

    private void redirectToMainMenu() {
        RedirectView redirectView = new RedirectView();
        redirectView.display();
    }
}
