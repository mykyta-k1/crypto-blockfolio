package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.CryptoEntity;
import java.time.LocalDateTime;

public class Cryptocurrency extends CryptoEntity implements Comparable<Cryptocurrency> {

    private double currentPrice;
    private double marketCap;
    private double volume24h;
    private double percentChange24h;
    private LocalDateTime lastUpdated;

    public Cryptocurrency(String symbol, String name, double currentPrice, double marketCap,
        double volume24h, double percentChange24h, LocalDateTime lastUpdated) {
        super(symbol, name);
        this.currentPrice = validatePositiveNumber(currentPrice, "Ціна");
        this.marketCap = validatePositiveNumber(marketCap, "Ринкова капіталізація");
        this.volume24h = validatePositiveNumber(volume24h, "Обсяг торгів за 24 години");
        this.percentChange24h = percentChange24h; // Відсоткова зміна може бути від'ємною
        this.lastUpdated = validateLastUpdated(lastUpdated);
    }

    private double validatePositiveNumber(double value, String fieldName) {
        if (value <= 0) {
            errors.add(fieldName + " повинно бути додатнім числом.");
        }
        return value;
    }

    private LocalDateTime validateLastUpdated(LocalDateTime lastUpdated) {
        if (lastUpdated == null || lastUpdated.isAfter(LocalDateTime.now())) {
            errors.add("Час останнього оновлення не може бути в майбутньому або пустим.");
        }
        return lastUpdated;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = validatePositiveNumber(currentPrice, "Ціна");
    }

    public double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(double marketCap) {
        this.marketCap = validatePositiveNumber(marketCap, "Ринкова капіталізація");
    }

    public double getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(double volume24h) {
        this.volume24h = validatePositiveNumber(volume24h, "Обсяг торгів за 24 години");
    }

    public double getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(double percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = validateLastUpdated(lastUpdated);
    }

    @Override
    public int compareTo(Cryptocurrency o) {
        return this.getSymbol().compareTo(o.getSymbol());
    }

    @Override
    public String toString() {
        return "Cryptocurrency{" +
            "currentPrice=" + currentPrice +
            ", marketCap=" + marketCap +
            ", volume24h=" + volume24h +
            ", percentChange24h=" + percentChange24h +
            ", lastUpdated=" + lastUpdated +
            '}';
    }
}
