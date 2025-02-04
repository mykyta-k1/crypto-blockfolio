package com.blockfolio.crypto.domain.impl;

import com.blockfolio.crypto.domain.ApiEndpoint;
import com.blockfolio.crypto.domain.contract.CoinGeckoApiService;
import com.blockfolio.crypto.domain.dto.CoinDetailsDto;
import com.blockfolio.crypto.domain.dto.CandleDto;
import com.blockfolio.crypto.domain.utils.GsonProvider;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CoinGeckoService implements CoinGeckoApiService {

    private static final Gson GSON = GsonProvider.GSON;

    @Override
    public CoinDetailsDto getCoinDetails(String coinId) {
        String endpoint = getCoinDetailsUrl(coinId);

        JsonElement responseElement = makeApiRequest(endpoint);
        JsonObject jsonObject = responseElement.getAsJsonObject();

        return GSON.fromJson(jsonObject, CoinDetailsDto.class);
    }

    @Override
    public List<CandleDto> getCandleBy30DaysOnCoin(String coinId) {
        String endpoint = getCoinOhlcUrl(coinId);

        JsonElement responseElement = makeApiRequest(endpoint);
        JsonArray jsonArray = responseElement.getAsJsonArray();

        List<CandleDto> candles30Day = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            CandleDto candleDto = GSON.fromJson(element, CandleDto.class);
            candles30Day.add(candleDto);
        }

        return candles30Day;
    }


    @Override
    public List<CoinDetailsDto> getCoinWithPage(int perPage, int page) {
        String endpoint = getCoinsMarketsUrl(perPage, page);

        JsonElement responseElement = makeApiRequest(endpoint);
        JsonArray jsonArray = responseElement.getAsJsonArray();

        List<CoinDetailsDto> coinWithPage = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            CoinDetailsDto coin = GSON.fromJson(element, CoinDetailsDto.class);
            coinWithPage.add(coin);
        }

        return coinWithPage;
    }

    @Override
    public List<CoinDetailsDto> searchCoinByNameOrId(String query) {
        String endpoint = getCoinSearchUrl(query);

        JsonElement responseElement = makeApiRequest(endpoint);
        JsonObject responseObject = responseElement.getAsJsonObject();
        JsonArray coinsArray = responseObject.getAsJsonArray("coins");

        List<CoinDetailsDto> foundCoins = new ArrayList<>();
        for (JsonElement element : coinsArray) {
            CoinDetailsDto coin = GSON.fromJson(element, CoinDetailsDto.class);
            foundCoins.add(coin);
        }

        return foundCoins;
    }

    private JsonElement makeApiRequest(String endpoint) {
        try {
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                return GSON.fromJson(reader, JsonElement.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCoinsMarketsUrl(int perPage, int page) {
        return ApiEndpoint.COINS_MARKETS.format(perPage, page);
    }

    private String getCoinOhlcUrl(String coinId) {
        return ApiEndpoint.COIN_OHLC.format(coinId);
    }

    private String getCoinSearchUrl(String query) {
        return ApiEndpoint.COIN_SEARCH.format(query);
    }

    private String getCoinDetailsUrl(String coinId) {
        return ApiEndpoint.COIN_DETAILS.format(coinId);
    }
}
