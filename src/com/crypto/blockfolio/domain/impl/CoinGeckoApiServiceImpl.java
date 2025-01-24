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

    private Cryptocurrency mapJsonToCryptocurrency(JsonObject jsonObject) {
        try {
            String symbol = jsonObject.get("symbol").getAsString().toUpperCase();
            String name = jsonObject.get("name").getAsString();

            double currentPrice =
                jsonObject.has("current_price") ? jsonObject.get("current_price").getAsDouble()
                    : 0.0;

            double marketCap =
                jsonObject.has("market_cap") ? jsonObject.get("market_cap").getAsDouble() : 0.0;

            double totalVolume =
                jsonObject.has("total_volume") ? jsonObject.get("total_volume").getAsDouble() : 0.0;

            double priceChange24h =
                jsonObject.has("price_change_24h") ? jsonObject.get("price_change_24h")
                    .getAsDouble() : 0.0;

            double priceChangePercentage24h =
                jsonObject.has("price_change_percentage_24h") ? jsonObject.get(
                    "price_change_percentage_24h").getAsDouble() : 0.0;

            LocalDateTime lastUpdated = jsonObject.has("last_updated")
                ? LocalDateTime.parse(jsonObject.get("last_updated").getAsString().replace("Z", ""))
                : LocalDateTime.now();

            return new Cryptocurrency(
                symbol,
                name,
                currentPrice,
                marketCap,
                totalVolume,
                priceChangePercentage24h,
                lastUpdated
            );
        } catch (Exception e) {
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
