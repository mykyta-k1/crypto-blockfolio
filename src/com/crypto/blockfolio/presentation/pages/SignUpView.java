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

    @Override
    public void display() {
        try {
            System.out.println("\n=== Реєстрація користувача ===");

            // Введення даних користувача
            System.out.print("Введіть логін: ");
            String username = scanner.nextLine();

            System.out.print("Введіть пароль: ");
            String password = scanner.nextLine();

            System.out.print("Введіть email: ");
            String email = scanner.nextLine();

            // Перевірка на існуючого користувача
            if (userService.getAll().stream().anyMatch(user ->
                user.getUsername().equalsIgnoreCase(username) ||
                    user.getEmail().equalsIgnoreCase(email))) {
                System.err.println("Помилка: Користувач із таким логіном або email вже існує.");
                redirectToMainMenu();
                return;
            }

            // Створення DTO користувача
            UserAddDto userAddDto = new UserAddDto(UUID.randomUUID(), username, password, email,
                null);

            // Генерація та відправлення коду підтвердження
            System.out.println("На вашу пошту надіслано код підтвердження.");
            signUpService.signUp(userAddDto, () -> {
                System.out.print("Введіть код підтвердження: ");
                return scanner.nextLine();
            });

            System.out.println("Користувач успішно зареєстрований!");
            DashBoardView dashBoardView = new DashBoardView();
            dashBoardView.display();
        } catch (SignUpException e) {
            System.err.println("Помилка реєстрації: " + e.getMessage());
            redirectToMainMenu();
        } catch (EntityArgumentException e) {
            System.err.println("Некоректні дані користувача:");
            e.getErrors().forEach(error -> System.err.println("- " + error));
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
