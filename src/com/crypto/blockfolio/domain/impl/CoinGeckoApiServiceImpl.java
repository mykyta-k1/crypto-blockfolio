package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CoinGeckoApiServiceImpl implements CoinGeckoApiService {

    private static final String API_BASE_URL = "https://api.coingecko.com/api/v3";
    private static final Gson GSON = new Gson();
    private static final int CACHE_DURATION_SECONDS = 60;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public Cryptocurrency getCryptocurrencyInfo(String name) {
        String cacheKey = "coin_" + name.toLowerCase();

        // Спроба отримати дані з кешу
        Optional<Cryptocurrency> cachedData = getCachedData(cacheKey);
        if (cachedData.isPresent()) {
            return cachedData.get();
        }

        // Запит до API
        String endpoint = String.format("%s/coins/%s", API_BASE_URL, name.toLowerCase());
        try {
            JsonObject jsonResponse = makeApiRequest(endpoint).getAsJsonObject();
            Cryptocurrency cryptocurrency = mapJsonToCryptocurrency(jsonResponse);

            // Збереження даних у кеш
            putCachedData(cacheKey, cryptocurrency);

            return cryptocurrency;
        } catch (Exception e) {
            throw new RuntimeException("Помилка під час отримання даних від CoinGecko API", e);
        }
    }

    @Override
    public List<Cryptocurrency> getAllCryptocurrencies() {
        String cacheKey = "all_coins";

        // Спроба отримати дані з кешу
        Optional<List<Cryptocurrency>> cachedData = getCachedData(cacheKey);
        if (cachedData.isPresent()) {
            return cachedData.get();
        }

        // Запит до API
        String endpoint = String.format("%s/coins/markets?vs_currency=usd", API_BASE_URL);
        try {
            JsonArray jsonResponse = makeApiRequest(endpoint).getAsJsonArray();
            List<Cryptocurrency> cryptocurrencies = new ArrayList<>();
            for (JsonElement element : jsonResponse) {
                cryptocurrencies.add(mapJsonToCryptocurrency(element.getAsJsonObject()));
            }

            // Збереження даних у кеш
            putCachedData(cacheKey, cryptocurrencies);

            return cryptocurrencies;
        } catch (Exception e) {
            throw new RuntimeException("Помилка під час отримання даних від CoinGecko API", e);
        }
    }

    private JsonElement makeApiRequest(String endpoint) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Не вдалося отримати дані від CoinGecko API. Код помилки: " +
                connection.getResponseCode());
        }

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            return GSON.fromJson(reader, JsonElement.class);
        }
    }
    /*
    private Cryptocurrency mapJsonToCryptocurrency(JsonObject jsonObject) {
        try {
            if (!jsonObject.has("name") || !jsonObject.has("current_price")) {
                throw new IllegalArgumentException(
                    "Відсутні необхідні поля у JSON об'єкті: " + jsonObject);
            }

            String name = jsonObject.get("name").getAsString();
            double currentPrice = jsonObject.get("current_price").getAsDouble();

            return new Cryptocurrency(UUID.randomUUID(), name, currentPrice);
        } catch (NullPointerException | IllegalStateException e) {
            System.err.println("Помилка обробки JSON: " + jsonObject);
            throw new RuntimeException("Неправильний формат даних від CoinGecko API", e);
        }
    }

     */

    private Cryptocurrency mapJsonToCryptocurrency(JsonObject jsonObject) {
        try {
            String name;
            double currentPrice;

            // Перевіряємо, який формат відповіді прийшов
            if (jsonObject.has("market_data")) {
                // Формат відповіді від /coins/{id}
                name = jsonObject.get("name").getAsString();
                currentPrice = jsonObject.getAsJsonObject("market_data")
                    .get("current_price")
                    .getAsJsonObject()
                    .get("usd")
                    .getAsDouble();
            } else {
                // Формат відповіді від /coins/markets
                name = jsonObject.get("name").getAsString();
                currentPrice = jsonObject.get("current_price").getAsDouble();
            }

            return new Cryptocurrency(UUID.randomUUID(), name, currentPrice);
        } catch (NullPointerException | IllegalStateException e) {
            System.err.println("Помилка обробки JSON: " + jsonObject);
            throw new RuntimeException("Неправильний формат даних від CoinGecko API", e);
        }
    }

    /**
     * Методи кешування відповіді від апі
     */
    private <T> Optional<T> getCachedData(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }
        return Optional.of((T) entry.getData());
    }

    private void putCachedData(String key, Object data) {
        cache.put(key, new CacheEntry(data));
    }

    private static class CacheEntry {

        private final Object data;
        private final LocalDateTime expirationTime;

        CacheEntry(Object data) {
            this.data = data;
            this.expirationTime = LocalDateTime.now().plusSeconds(CACHE_DURATION_SECONDS);
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expirationTime);
        }

        public Object getData() {
            return data;
        }
    }
}
