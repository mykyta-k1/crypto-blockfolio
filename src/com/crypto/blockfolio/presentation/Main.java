package com.crypto.blockfolio.presentation;

import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.domain.impl.CoinGeckoApiServiceImpl;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Ініціалізація сервісу CoinGecko API
        CoinGeckoApiService apiService = new CoinGeckoApiServiceImpl();

        // Тест №1: Отримання інформації про конкретну криптовалюту
        System.out.println("--- Тест №1: Інформація про Bitcoin ---");
        Cryptocurrency bitcoin1 = apiService.getCryptocurrencyInfo("bitcoin");
        System.out.println("Результат: " + bitcoin1);

        // Затримка перед повторним викликом
        sleep(2000);

        Cryptocurrency bitcoin2 = apiService.getCryptocurrencyInfo("bitcoin");
        System.out.println("Результат: " + bitcoin2);

        // Тест №2: Отримання інформації про всі криптовалюти
        System.out.println("--- Тест №2: Інформація про всі криптовалюти ---");
        List<Cryptocurrency> allCryptocurrencies1 = apiService.getAllCryptocurrencies();
        System.out.println("Кількість монет: " + allCryptocurrencies1.size());

        // Затримка перед повторним викликом
        sleep(2000);

        List<Cryptocurrency> allCryptocurrencies2 = apiService.getAllCryptocurrencies();
        System.out.println("Кількість монет: " + allCryptocurrencies2.size());

        // Тест завершено
        System.out.println("--- Тестування завершено ---");
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
