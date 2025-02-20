package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class CoinGeckoApiServiceImpl implements CoinGeckoApiService {

    private static final String API_BASE_URL = "https://api.coingecko.com/api/v3";
    private static final Gson GSON = new Gson();

    private final CryptocurrencyRepository cryptocurrencyRepository;

    public CoinGeckoApiServiceImpl(CryptocurrencyRepository cryptocurrencyRepository) {
        this.cryptocurrencyRepository = cryptocurrencyRepository;
    }

    @Override
    public Cryptocurrency getCryptocurrencyInfo(String symbol) {
        // Використовуємо дані лише з репозиторію
        return cryptocurrencyRepository.findBySymbol(symbol)
            .orElseThrow(() -> new RuntimeException(
                "Криптовалюта з символом " + symbol + " не знайдена у файлі."));
    }

    @Override
    public List<Cryptocurrency> getAllCryptocurrencies() {
        try {
            // Спроба отримати дані з API
            List<Cryptocurrency> cryptocurrencies = fetchAllCryptocurrenciesFromApi();
            if (cryptocurrencies.isEmpty()) {
                throw new RuntimeException("API повернуло порожній список криптовалют.");
            }

            // Збереження даних у репозиторій
            cryptocurrencies.forEach(cryptocurrencyRepository::add);
            return cryptocurrencies;

        } catch (Exception e) {
            // Логування помилки API
            System.err.printf("Помилка запиту до API: %s%n", e.getMessage());

            // Використання даних із файлу
            try {
                List<Cryptocurrency> fallbackData = List.copyOf(cryptocurrencyRepository.findAll());
                if (fallbackData.isEmpty()) {
                    throw new RuntimeException("Дані з репозиторію недоступні.");
                }
                System.out.println("Дані завантажено з файлу.");
                return fallbackData;

            } catch (Exception fallbackException) {
                System.err.printf("Помилка доступу до даних з файлу: %s%n",
                    fallbackException.getMessage());
                throw new RuntimeException("Не вдалося отримати дані ні з API, ні з файлу.");
            }
        }
    }


    private List<Cryptocurrency> fetchAllCryptocurrenciesFromApi() throws Exception {
        try {
            String endpoint = String.format("%s/coins/markets?vs_currency=usd", API_BASE_URL);
            JsonArray jsonResponse = makeApiRequest(endpoint).getAsJsonArray();

            List<Cryptocurrency> cryptocurrencies = new ArrayList<>();
            for (JsonElement element : jsonResponse) {
                JsonObject coinData = element.getAsJsonObject();
                cryptocurrencies.add(new Cryptocurrency(
                    coinData.get("symbol").getAsString().toUpperCase(),
                    coinData.get("name").getAsString(),
                    coinData.get("current_price").getAsDouble(),
                    coinData.has("market_cap") ? coinData.get("market_cap").getAsDouble() : 0.0,
                    coinData.has("total_volume") ? coinData.get("total_volume").getAsDouble() : 0.0,
                    coinData.has("price_change_percentage_24h")
                        ? coinData.get("price_change_percentage_24h").getAsDouble() : 0.0,
                    LocalDateTime.now()
                ));
            }

            return cryptocurrencies;

        } catch (Exception e) {
            throw new RuntimeException(
                "Не вдалося отримати дані від CoinGecko API. " + e.getMessage());
        }
    }


    private JsonElement makeApiRequest(String endpoint) throws Exception {
        //System.out.printf("Запит до API: %s%n", endpoint);

        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException(
                "Не вдалося отримати дані від CoinGecko API" + endpoint + ". Код помилки: "
                    + responseCode);
        }

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            return GSON.fromJson(reader, JsonElement.class);
        }
    }

}
