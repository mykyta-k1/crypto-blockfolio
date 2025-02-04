package com.blockfolio.crypto.domain;

public enum ApiEndpoint {
    // Запит на отримання списку монет із пагінацією
    COINS_MARKETS("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=%d&page=%d&sparkline=false"),

    // Запит на отримання графіку свічкового типу (для монети за останні 30 днів)
    COIN_OHLC("https://api.coingecko.com/api/v3/coins/%s/ohlc?vs_currency=usd&days=30"),

    // Запит під час пошуку конкретної монети (підставляємо пошуковий запит)
    COIN_SEARCH("https://api.coingecko.com/api/v3/search?query=%s"),

    // Запит по конкретній монеті (підставляємо id монети)
    COIN_DETAILS("https://api.coingecko.com/api/v3/coins/%s?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false");

    private final String url;

    ApiEndpoint(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String format(Object... args) {
        return String.format(url, args);
    }
}

