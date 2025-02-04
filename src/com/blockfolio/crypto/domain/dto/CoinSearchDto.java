package com.blockfolio.crypto.domain.dto;

import com.google.gson.annotations.SerializedName;

public class CoinSearchDto {

    @SerializedName("id")
    private String coinId;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("market_cap_rank")
    private Integer marketCapRank;

    public CoinSearchDto(String coinId, String symbol) {
        this.coinId = coinId;
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCoinId() {
        return coinId;
    }

    @Override
    public String toString() {
        return "CoinDto{" +
            "name='" + coinId + '\'' +
            ", symbol='" + symbol + '\'' +
            '}';
    }
}
