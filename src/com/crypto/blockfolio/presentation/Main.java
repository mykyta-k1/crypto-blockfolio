package com.crypto.blockfolio.presentation;


import com.crypto.blockfolio.presentation.pages.DashBoardView;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Main {

    public static void main(String[] args) throws NotImplementedException {
        ApplicationContext applicationContext = ApplicationContext.getInstance();

        //CoinGeckoApiService coinGeckoApiService = ApplicationContext.getCoinGeckoApiService();
        //coinGeckoApiService.getAllCryptocurrencies();

        DashBoardView dashBoardView = new DashBoardView();
        dashBoardView.display();
    }
    /*
    public static void testUser() {
        System.out.println("=== Тест реєстрації користувача ===");

        // Ініціалізація даних користувача
        String username = "testuser";
        String password = "123PassWord";
        String email = "c.kryvobokov.mykyta@student.uzhnu.edu.ua";

        // Створення DTO для користувача
        UserAddDto userAddDto = new UserAddDto(UUID.randomUUID(), username, password, email, null);

        try {
            // Використання SignUpService для реєстрації
            signUpService.signUp(userAddDto, () -> {
                System.out.println("На вашу пошту надіслано код підтвердження.");
                System.out.print("Введіть код підтвердження: ");

                // Імітація введення коду користувачем
                Scanner scanner = new Scanner(System.in);
                return scanner.nextLine();
            });

            System.out.println("Користувач успішно зареєстрований та авторизований.");

            // Перевірка, чи користувач доданий у репозиторій
            User createdUser = userService.getByUsername(username);
            System.out.println("Дані збереженого користувача: " + createdUser);

        } catch (SignUpException e) {
            System.err.println("Помилка реєстрації: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непередбачена помилка: " + e.getMessage());
        }

        System.out.println("=== Тест реєстрації завершено ===");
        testPortfolioOperations();
    }
    */
    /*
    public static void testPortfolioOperations() {
        System.out.println("=== Тест операцій з портфелями ===");

        try {
            // Авторизація користувача
            User user = authService.getUser();
            if (user == null) {
                System.err.println("Користувач не авторизований.");
                return;
            }

            // Створення першого портфеля через DTO
            UUID portfolioId1 = UUID.randomUUID();
            PortfolioAddDto portfolioDto1 = new PortfolioAddDto(portfolioId1, user.getId(),
                "Портфель 1", null, null);
            portfolioService.addPortfolio(portfolioDto1);
            user.addPortfolio(portfolioId1);
            userService.add(new UserAddDto(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getPortfolios()));

            // Створення другого портфеля через DTO
            UUID portfolioId2 = UUID.randomUUID();
            PortfolioAddDto portfolioDto2 = new PortfolioAddDto(portfolioId2, user.getId(),
                "Портфель 2", null, null);
            portfolioService.addPortfolio(portfolioDto2);
            user.addPortfolio(portfolioId2);
            userService.add(new UserAddDto(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getPortfolios()));

            // Видалення першого портфеля через ID
            portfolioService.deletePortfolio(portfolioId1);
            user.removePortfolio(portfolioId1);
            userService.add(new UserAddDto(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getPortfolios()));
            System.out.println("Портфель 'Портфель 1' успішно видалено.");

            // Отримання криптовалюти з API через CoinGeckoApiService
            String coinName = "bitcoin"; // Приклад: Bitcoin
            Cryptocurrency cryptocurrency = coinGeckoApiService.getCryptocurrencyInfo(coinName);
            System.out.println("Отримано криптовалюту з API: " + cryptocurrency);

            // Додавання криптовалюти до другого портфеля
            portfolioService.addCryptocurrencyToPortfolio(portfolioId2, cryptocurrency);
            System.out.println("Криптовалюта додана до 'Портфель 2'.");

            // Вивід оновленого портфеля через сервіс
            Portfolio updatedPortfolio = portfolioService.getPortfolioById(portfolioId2);
            System.out.println("Оновлений 'Портфель_2': " + updatedPortfolio);

        } catch (Exception e) {
            System.err.println("Сталася помилка: " + e.getMessage());
        }

        System.out.println("=== Тест завершено ===");
    }

     */


}
