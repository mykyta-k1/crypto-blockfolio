package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class CryptocurrenciesView implements ViewService {

    private static final int PAGE_SIZE = 10; // Кількість монет на одній сторінці
    private final CryptocurrencyService cryptocurrencyService;

    public CryptocurrenciesView(CryptocurrencyService cryptocurrencyService) {
        this.cryptocurrencyService = cryptocurrencyService;
    }

    @Override
    public void display() {
        Set<Cryptocurrency> cryptocurrencies = cryptocurrencyService.getAll(); // Отримання всіх монет
        List<Cryptocurrency> sortedCryptocurrencies = cryptocurrencies.stream()
            .sorted(Comparator.comparingDouble(Cryptocurrency::getMarketCap)
                .reversed()) // Сортування за ринковою капіталізацією
            .collect(Collectors.toList());

        if (sortedCryptocurrencies.isEmpty()) {
            System.out.println("Список криптовалют порожній.");
            return;
        }

        int totalPages = (int) Math.ceil((double) sortedCryptocurrencies.size() / PAGE_SIZE);
        int currentPage = 1;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayPage(sortedCryptocurrencies, currentPage, totalPages);

            System.out.println("\n[0] Повернутися до головного меню");
            System.out.println("[←] Попередня сторінка");
            System.out.println("[→] Наступна сторінка");

            System.out.print("Ваш вибір: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "0" -> {
                    System.out.println("Повернення до головного меню...");
                    return;
                }
                case "←" -> {
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("Це перша сторінка.");
                    }
                }
                case "→" -> {
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

    private void displayPage(List<Cryptocurrency> cryptocurrencies, int currentPage,
        int totalPages) {
        System.out.println(
            "=== Список криптовалют (Сторінка " + currentPage + " з " + totalPages + ") ===");

        int start = (currentPage - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, cryptocurrencies.size());

        for (int i = start; i < end; i++) {
            Cryptocurrency crypto = cryptocurrencies.get(i);
            displayCryptocurrency(crypto, i + 1);
        }
    }

    private void displayCryptocurrency(Cryptocurrency cryptocurrency, int index) {
        System.out.printf(
            "[%d] Назва: %s | Символ: %s | Ціна: $%.2f | Ринкова капіталізація: $%.2f | Обсяг торгів (24г): $%.2f | Зміна за 24г: %.2f%%\n",
            index,
            cryptocurrency.getName(),
            cryptocurrency.getSymbol(),
            cryptocurrency.getCurrentPrice(),
            cryptocurrency.getMarketCap(),
            cryptocurrency.getVolume24h(),
            cryptocurrency.getPercentChange24h()
        );
    }
}
