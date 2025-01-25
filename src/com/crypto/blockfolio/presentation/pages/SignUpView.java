package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.dto.UserAddDto;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.presentation.Main;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Scanner;
import java.util.UUID;

public class SignUpView implements ViewService {

    @Override
    public void display() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=== Реєстрація користувача ===");

            // Введення даних користувача
            System.out.print("Введіть логін: ");
            String username = scanner.nextLine();

            System.out.print("Введіть пароль: ");
            String password = scanner.nextLine();

            System.out.print("Введіть email: ");
            String email = scanner.nextLine();

            // Перевірка, чи користувач із такою поштою або логіном існує
            if (Main.getSignUpService().userExists(username, email)) {
                System.err.println("Помилка: Користувач із таким логіном або email вже існує.");
                return;
            }

            // Створення DTO користувача
            UserAddDto userAddDto = new UserAddDto(UUID.randomUUID(), username, password, email,
                null);

            // Генерація та відправлення коду підтвердження
            System.out.println("На вашу пошту надіслано код підтвердження.");
            Main.getSignUpService().signUp(userAddDto, () -> {
                System.out.print("Введіть код підтвердження: ");
                return scanner.nextLine();
            });

            System.out.println("Користувач успішно зареєстрований!");
            DashBoardView dashBoardView = new DashBoardView();
            dashBoardView.display();

        } catch (EntityArgumentException e) {
            System.err.println("Помилки при створенні користувача:");
            e.getErrors().forEach(error -> System.err.println("- " + error));
        } catch (Exception e) {
            System.err.println("Сталася помилка: " + e.getMessage());
        }
    }
}
