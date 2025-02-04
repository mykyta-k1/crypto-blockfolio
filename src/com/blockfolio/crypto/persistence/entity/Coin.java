package com.blockfolio.crypto.persistence.entity;
import com.blockfolio.crypto.domain.utils.TimeUtils;
import com.blockfolio.crypto.persistence.Entity;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Coin extends Entity<String> {

    private String name;
    private String symbol;
    private String image;

    @SerializedName("current_price")
    private BigDecimal currentPrice;

    @SerializedName("market_cap")
    private BigDecimal marketCap;

    @SerializedName("market_cap_rank")
    private Integer marketCapRank;

    @SerializedName("total_volume")
    private BigDecimal totalVolume;

    @SerializedName("high_24h")
    private BigDecimal high24h;

    @SerializedName("low_24h")
    private BigDecimal low24h;

    @SerializedName("price_change_24h")
    private BigDecimal priceChange24h;

    @SerializedName("price_change_percentage_24h")
    private BigDecimal priceChangePercentage24h;

    @SerializedName("total_supply")
    private Integer totalSupply;

    @SerializedName("max_supply")
    private Integer maxSupply;

    @SerializedName("last_updated")
    private LocalDateTime lastUpdated;

    public Coin(String id, String name, String symbol, String image, BigDecimal currentPrice,
        BigDecimal marketCap, Integer marketCapRank, BigDecimal totalVolume, BigDecimal high24h, BigDecimal low24h,
        BigDecimal priceChange24h, BigDecimal priceChangePercentage24h, Integer totalSupply, Integer maxSupply,
        String lastUpdated) {
        super(id);
        this.name = name;
        this.symbol = symbol;
        this.image = image;
        this.currentPrice = currentPrice;
        this.marketCap = marketCap;
        this.marketCapRank = marketCapRank;
        this.totalVolume = totalVolume;
        this.high24h = high24h;
        this.low24h = low24h;
        this.priceChange24h = priceChange24h;
        this.priceChangePercentage24h = priceChangePercentage24h;
        this.totalSupply = totalSupply;
        this.maxSupply = maxSupply;
        this.lastUpdated = TimeUtils.getLocalDateTimeFromIso(lastUpdated);

    }


}
