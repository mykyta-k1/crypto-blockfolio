package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class CryptocurrenciesView implements ViewService {

    private static final int PAGE_SIZE = 20; // Кількість монет на сторінку
    private final CryptocurrencyService cryptocurrencyService;
    private final Scanner scanner;

    public CryptocurrenciesView() {
        this.cryptocurrencyService = ApplicationContext.getCryptocurrencyService();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void display() {
        System.out.println("=== Перегляд усіх криптовалют ===");

        // Використання CompletableFuture для асинхронного завантаження
        List<Cryptocurrency> cryptocurrencies;
        try {
            cryptocurrencies = CompletableFuture.supplyAsync(() -> {
                try {
                    return cryptocurrencyService.getAllCryptocurrencies();
                } catch (Exception e) {
                    System.err.printf("Помилка завантаження даних: %s%n", e.getMessage());
                    return new ArrayList<Cryptocurrency>();
                }
            }).thenApply(result -> result).join(); // Уточнення типу
        } catch (Exception e) {
            System.err.printf("Помилка під час завантаження: %s%n", e.getMessage());
            return;
        }

        if (cryptocurrencies.isEmpty()) {
            System.out.println("Немає даних про криптовалюти.");
            return;
        }

        int totalPages = (int) Math.ceil((double) cryptocurrencies.size() / PAGE_SIZE);
        int currentPage = 1;

        while (true) {
            // Виведення криптовалют на поточній сторінці
            int start = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, cryptocurrencies.size());

            for (int i = start; i < end; i++) {
                Cryptocurrency crypto = cryptocurrencies.get(i);
                displayCryptocurrencyDetails(i + 1, crypto);
            }

            // Показуємо меню пагінації
            System.out.println("\n[0] Повернутися назад");
            if (currentPage > 1) {
                System.out.println("[p] Попередня сторінка");
            }
            if (currentPage < totalPages) {
                System.out.println("[n] Наступна сторінка");
            }
            System.out.printf("\nСторінка %d з %d%n", currentPage, totalPages);
            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "0" -> {
                    return; // Повернення до попереднього меню
                }
                case "p" -> {
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("Це перша сторінка.");
                    }
                }
                case "n" -> {
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("Це остання сторінка.");
                    }
                }
                default -> System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }


    private void displayCryptocurrencyDetails(int index, Cryptocurrency crypto) {
        // Формат для стандартних чисел
        DecimalFormat standardFormat = new DecimalFormat("#,##0.00");
        // Формат для малих чисел (з більшою точністю)
        DecimalFormat smallNumberFormat = new DecimalFormat("#,##0.00000000");

        // Поточна ціна
        String currentPriceFormatted;
        if (crypto.getCurrentPrice() < 1) {
            currentPriceFormatted = smallNumberFormat.format(crypto.getCurrentPrice());
        } else {
            currentPriceFormatted = standardFormat.format(crypto.getCurrentPrice());
        }

        // Вивід
        System.out.printf("\n%d. Назва: %s (%s)%n", index, crypto.getName(), crypto.getSymbol());
        System.out.printf("   Поточна ціна: %s USD%n", currentPriceFormatted);
        System.out.printf("   Ринкова капіталізація: %s USD%n",
            standardFormat.format(crypto.getMarketCap()));
        System.out.printf("   Обсяг за 24 години: %s USD%n",
            standardFormat.format(crypto.getVolume24h()));
        System.out.printf("   Зміна за 24 години: %.2f%%%n", crypto.getPercentChange24h());
    }
}
