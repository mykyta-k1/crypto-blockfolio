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
import java.util.concurrent.ConcurrentHashMap;

class CoinGeckoApiServiceImpl implements CoinGeckoApiService {

    private static final String API_BASE_URL = "https://api.coingecko.com/api/v3";
    private static final Gson GSON = new Gson();
    private static final int CACHE_DURATION_SECONDS = 60;

    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public Cryptocurrency getCryptocurrencyInfo(String name) {
        String cacheKey = "coin_" + name.toLowerCase();

        // Перевірка в кеші
        Optional<Cryptocurrency> cachedData = getCachedData(cacheKey);
        if (cachedData.isPresent()) {
            return cachedData.get();
        }

        // Запит до API
        String endpoint = String.format("%s/coins/%s", API_BASE_URL, name.toLowerCase());
        try {
            JsonObject jsonResponse = makeApiRequest(endpoint).getAsJsonObject();
            Cryptocurrency cryptocurrency = mapJsonToCryptocurrency(jsonResponse);

            // Додавання до кешу
            putCachedData(cacheKey, cryptocurrency);

            return cryptocurrency;
        } catch (Exception e) {
            throw new RuntimeException("Помилка під час отримання даних від CoinGecko API", e);
        }
    }

    @Override
    public List<Cryptocurrency> getAllCryptocurrencies() {
        String cacheKey = "all_coins";

        // Перевірка в кеші
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
                JsonObject coinData = element.getAsJsonObject();
                cryptocurrencies.add(new Cryptocurrency(
                    coinData.get("symbol").getAsString().toUpperCase(),
                    coinData.get("name").getAsString(),
                    coinData.get("current_price").getAsDouble(),
                    coinData.has("market_cap") ? coinData.get("market_cap").getAsDouble() : 0.0,
                    coinData.has("total_volume") ? coinData.get("total_volume").getAsDouble() : 0.0,
                    coinData.has("price_change_percentage_24h") ?
                        coinData.get("price_change_percentage_24h").getAsDouble() : 0.0,
                    LocalDateTime.now()
                ));
            }

            // Додавання до кешу
            putCachedData(cacheKey, cryptocurrencies);

            return cryptocurrencies;
        } catch (Exception e) {
            throw new RuntimeException("Помилка під час отримання даних від CoinGecko API", e);
        }
    }

    /**
     * Метод для здійснення HTTP-запиту до API.
     */
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

    /**
     * Метод для мапінгу JSON-об'єкта в сутність `Cryptocurrency`.
     */
    private Cryptocurrency mapJsonToCryptocurrency(JsonObject jsonObject) {
        try {
            return new Cryptocurrency(
                jsonObject.get("symbol").getAsString().toUpperCase(),
                jsonObject.get("name").getAsString(),
                jsonObject.get("market_data").getAsJsonObject().get("current_price")
                    .getAsJsonObject().get("usd").getAsDouble(),
                jsonObject.get("market_data").getAsJsonObject().get("market_cap")
                    .getAsJsonObject().get("usd").getAsDouble(),
                jsonObject.get("market_data").getAsJsonObject().get("total_volume")
                    .getAsJsonObject().get("usd").getAsDouble(),
                jsonObject.get("market_data").getAsJsonObject()
                    .get("price_change_percentage_24h").getAsDouble(),
                LocalDateTime.now()
            );
        } catch (Exception e) {
            System.err.println("Помилка обробки JSON: " + jsonObject);
            throw new RuntimeException("Неправильний формат даних від CoinGecko API", e);
        }
    }

    /**
     * Методи кешування для зберігання даних.
     */
    private <T> Optional<T> getCachedData(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }
        System.out.println("[CASH] Used");
        return Optional.of((T) entry.getData());
    }

    private void putCachedData(String key, Object data) {
        cache.put(key, new CacheEntry(data));
    }

    /**
     * Внутрішній клас для кешування даних.
     */
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
