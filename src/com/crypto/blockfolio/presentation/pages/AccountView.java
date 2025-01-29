package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.domain.contract.UserService;
import com.crypto.blockfolio.domain.exception.AuthException;
import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Клас відповідає за відображення сторінки акаунту
 */
public class AccountView implements ViewService {

    private final AuthService authService;
    private final UserService userService;
    private final CryptocurrencyService cryptocurrencyService;
    private final Scanner scanner;

    public AccountView() {
        this.authService = ApplicationContext.getAuthService();
        this.userService = ApplicationContext.getUserService();
        this.cryptocurrencyService = ApplicationContext.getCryptocurrencyService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Відображає інформацію про акаунт користувача, надає можливість змінити нікнейм, email,
     * згенерувати звіт або вийти з акаунту.
     *
     * @throws AuthException якщо користувач не автентифікований.
     */
    @Override
    public void display() {
        try {
            User currentUser = authService.getUser();

            while (true) {
                System.out.println("\n=== 🛠️ Інформація про акаунт ===");
                System.out.println("📧 Email: " + currentUser.getEmail());
                System.out.println("👤 Нікнейм: " + currentUser.getUsername());
                System.out.println("📅 Дата створення: " + currentUser.getCreatedAt());
                System.out.println("\n1 👤 Змінити нікнейм");
                System.out.println("2. 📧 Змінити email");
                System.out.println("3. \uD83D\uDCC4 Генерувати звіт");
                System.out.println("4. \uD83D\uDD12 Вийти з акаунту");
                System.out.println("0. \uD83D\uDD19 Повернутися назад");

                System.out.print("✨ Оберіть опцію: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        changeUsername(currentUser);
                        break;
                    case "2":
                        changeEmail(currentUser);
                        break;
                    case "3":
                        generateCryptocurrencyReport();
                    case "4":
                        logoutAccount();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Невірний вибір. Спробуйте ще раз.");
                }
            }
        } catch (AuthException e) {
            System.out.println("Помилка: Ви не автентифіковані.");
        }
    }

    /**
     * Змінює нікнейм поточного користувача.
     *
     * @param currentUser поточний користувач.
     */
    private void changeUsername(User currentUser) {
        System.out.print("Введіть новий нікнейм: ");
        String newUsername = scanner.nextLine();

        try {
            currentUser.setUsername(newUsername);
            userService.update(currentUser);
            System.out.println("Нікнейм успішно змінено!");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    /**
     * Змінює email поточного користувача після перевірки поточного email.
     *
     * @param currentUser поточний користувач.
     */
    private void changeEmail(User currentUser) {
        System.out.print("Введіть поточний email для підтвердження: ");
        String currentEmail = scanner.nextLine();

        if (!currentEmail.equals(currentUser.getEmail())) {
            System.out.println("Помилка: Невірний поточний email.");
            return;
        }

        System.out.print("Введіть новий email: ");
        String newEmail = scanner.nextLine();

        try {
            currentUser.setEmail(newEmail);
            userService.update(currentUser);
            System.out.println("Email успішно змінено!");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }

    /**
     * Виходить з акаунту користувача, завершуючи автентифікаційну сесію.
     */
    private void logoutAccount() {
        System.out.println(
            "Ви вийшли з акаунту, при наступному запуску програми потрібно увійти знову.");
        authService.logout();
    }

    /**
     * Генерує звіт по криптовалютах для поточного користувача.
     *
     * @throws Exception якщо виникла помилка при створенні звіту.
     */
    private void generateCryptocurrencyReport() {
        System.out.print(
            "Введіть шлях для збереження (або залиште порожнім для директорії за замовчуванням): ");
        String inputPath = scanner.nextLine().trim();

        Path savePath = cryptocurrencyService.resolveSavePath(inputPath);

        try {
            cryptocurrencyService.generateReport(savePath,
                crypto -> true);
            System.out.println("Звіт успішно збережено в: " + savePath);
        } catch (Exception e) {
            System.err.println("Помилка при створенні звіту: " + e.getMessage());
        }
    }
}
