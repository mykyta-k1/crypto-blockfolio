package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.presentation.ViewService;
import java.util.Scanner;

/**
 * Redirect page to choose between registration or login.
 */
public class RedirectView implements ViewService {

    @Override
    public void display() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Вибір дії ===");
        System.out.println("1. Реєстрація");
        System.out.println("2. Увійти");
        System.out.println("3. Вийти");
        System.out.print("Введіть номер дії: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> {
                    SignUpView signUpView = new SignUpView();
                    signUpView.display();
                }
                case 2 -> {
                    AuthView authView = new AuthView();
                    authView.display();
                }
                case 3 -> {
                    System.out.println("Дякуємо за використання Blockfolio! До побачення.");
                    System.exit(0);
                }
                default -> System.out.println("Некоректний вибір. Спробуйте ще раз.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Некоректний формат введення. Спробуйте ще раз.");
        }
    }
}

