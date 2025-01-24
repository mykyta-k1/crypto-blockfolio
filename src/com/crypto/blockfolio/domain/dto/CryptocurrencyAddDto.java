package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.CryptoEntity;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.time.LocalDateTime;

public class CryptocurrencyAddDto extends CryptoEntity {

    private final double currentPrice;
    private final double marketCap;
    private final double volume24h;
    private final double percentChange24h;
    private final LocalDateTime lastUpdated;

    public CryptocurrencyAddDto(
        String symbol, String name, double currentPrice, double marketCap,
        double volume24h, double percentChange24h, LocalDateTime lastUpdated
    ) {
        super(symbol,
            name); // Ідентифікатор для `Cryptocurrency` не потрібен, використовуємо символ.
        this.currentPrice = validatePositiveNumber(currentPrice, "Ціна");
        this.marketCap = validatePositiveNumber(marketCap, "Ринкова капіталізація");
        this.volume24h = validatePositiveNumber(volume24h, "Обсяг торгів за 24 години");
        this.percentChange24h = percentChange24h; // Відсоткова зміна може бути від'ємною
        this.lastUpdated = validateLastUpdated(lastUpdated);
    }

    private String validateSymbol(String symbol) {
        ValidationUtils.validateRequired(symbol, "Символ монети", errors);
        ValidationUtils.validateLength(symbol, 1, 10, "Символ монети", errors);
        ValidationUtils.validatePattern(symbol, "^[A-Z0-9]+$", "Символ монети", errors);
        return symbol;
    }

    private String validateName(String name) {
        ValidationUtils.validateRequired(name, "Назва монети", errors);
        ValidationUtils.validateLength(name, 2, 50, "Назва монети", errors);
        ValidationUtils.validatePattern(name, "^[a-zA-Z0-9\\s]+$", "Назва монети", errors);
        return name;
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

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getMarketCap() {
        return marketCap;
    }

    public double getVolume24h() {
        return volume24h;
    }

    public double getPercentChange24h() {
        return percentChange24h;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
