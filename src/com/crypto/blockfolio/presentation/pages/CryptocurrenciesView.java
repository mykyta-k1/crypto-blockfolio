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

    private static final int PAGE_SIZE = 20;
    private final CryptocurrencyService cryptocurrencyService;
    private final Scanner scanner;

    public CryptocurrenciesView() {
        this.cryptocurrencyService = ApplicationContext.getCryptocurrencyService();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Відображає каталог криптовалют із підтримкою пагінації. Дані завантажуються асинхронно через
     * {@link CryptocurrencyService}. У разі помилок завантаження виводить повідомлення про
     * недоступність даних.
     */
    @Override
    public void display() {
        System.out.println("\n📊 КАТАЛОГ КРИПТОВАЛЮТ");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Використання CompletableFuture для асинхронного завантаження
        List<Cryptocurrency> cryptocurrencies;
        try {
            cryptocurrencies = CompletableFuture.supplyAsync(() -> {
                try {
                    return cryptocurrencyService.getAllCryptocurrencies();
                } catch (Exception e) {
                    System.err.println("⚠️ Помилка завантаження даних: " + e.getMessage());
                    return new ArrayList<Cryptocurrency>();
                }
            }).thenApply(result -> result).join();
        } catch (Exception e) {
            System.out.println("📭 На жаль, дані про криптовалюти тимчасово недоступні");
            return;
        }

        if (cryptocurrencies.isEmpty()) {
            System.out.println("📭 На жаль, дані про криптовалюти тимчасово недоступні");
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

            System.out.println("\n📑 НАВІГАЦІЯ");
            System.out.println("━━━━━━━━━━━");
            System.out.println("0. 🔙 Повернутися");
            if (currentPage > 1) {
                System.out.println("◀️ [P] Попередня сторінка");
            }
            if (currentPage < totalPages) {
                System.out.println("▶️ [N] Наступна сторінка");
            }
            System.out.printf("\n📄 Сторінка %d з %d%n", currentPage, totalPages);
            System.out.print("✨ Ваш вибір: ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "0" -> {
                    return;
                }
                case "p" -> {
                    if (currentPage > 1) {
                        currentPage--;
                    } else {
                        System.out.println("⚠️ Ви вже на першій сторінці");
                    }
                }
                case "n" -> {
                    if (currentPage < totalPages) {
                        currentPage++;
                    } else {
                        System.out.println("⚠️ Ви вже на останній сторінці");
                    }
                }
                default -> System.out.println("❌ Невірний вибір. Оберіть доступну опцію.");
            }
        }
    }

    /**
     * Відображає детальну інформацію про криптовалюту, включаючи символ, назву, ціну,
     * капіталізацію, об'єм за 24 години та зміну ціни.
     *
     * @param index  позиція криптовалюти у списку.
     * @param crypto екземпляр криптовалюти для відображення.
     */
    private void displayCryptocurrencyDetails(int index, Cryptocurrency crypto) {
        // Формат для стандартних чисел
        DecimalFormat standardFormat = new DecimalFormat("#,##0.00");
        // Формат для малих чисел (з більшою точністю)
        DecimalFormat smallNumberFormat = new DecimalFormat("#,##0.00000000");

        String currentPriceFormatted;
        if (crypto.getCurrentPrice() < 1) {
            currentPriceFormatted = smallNumberFormat.format(crypto.getCurrentPrice());
        } else {
            currentPriceFormatted = standardFormat.format(crypto.getCurrentPrice());
        }
        System.out.printf("\n%d. %s %s (%s)%n",
            index,
            crypto.getSymbol().equals("BTC") ? "₿" : "🪙",
            crypto.getName(),
            crypto.getSymbol()
        );
        System.out.printf("   💵 Ціна: %s USD%n", currentPriceFormatted);
        System.out.printf("   💰 Капіталізація: %s USD%n",
            standardFormat.format(crypto.getMarketCap()));
        System.out.printf("   📈 Об'єм (24г): %s USD%n",
            standardFormat.format(crypto.getVolume24h()));
        double change = crypto.getPercentChange24h();
        String changeEmoji = change > 0 ? "🟢" : (change < 0 ? "🔴" : "⚪");
        System.out.printf("   %s Зміна (24г): %+.2f%%%n", changeEmoji, change);
    }
}
