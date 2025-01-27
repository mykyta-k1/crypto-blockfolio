package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.CryptoEntity;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) для додавання нової криптовалюти. Використовується для передачі даних,
 * необхідних для створення нової криптовалюти в системі.
 */
public final class CryptocurrencyAddDto extends CryptoEntity {

    /**
     * Поточна ціна криптовалюти.
     */
    private final double currentPrice;
    /**
     * Ринкова капіталізація.
     */
    private final double marketCap;
    /**
     * Обсяг торгів за останні 24 години.
     */
    private final double volume24h;
    /**
     * Відсоткова зміна за останні 24 години.
     */
    private final double percentChange24h;
    /**
     * Час останнього оновлення.
     */
    private final LocalDateTime lastUpdated;

    /**
     * Конструктор для створення нового екземпляра {@link CryptocurrencyAddDto}.
     *
     * @param symbol           символ криптовалюти (наприклад, BTC).
     * @param name             назва криптовалюти (наприклад, Bitcoin).
     * @param currentPrice     поточна ціна криптовалюти.
     * @param marketCap        ринкова капіталізація криптовалюти.
     * @param volume24h        обсяг торгів за останні 24 години.
     * @param percentChange24h відсоткова зміна ціни за останні 24 години.
     * @param lastUpdated      час останнього оновлення інформації.
     * @throws IllegalArgumentException якщо будь-яке з числових значень є недопустимим.
     */
    public CryptocurrencyAddDto(
        String symbol, String name, double currentPrice, double marketCap,
        double volume24h, double percentChange24h, LocalDateTime lastUpdated
    ) {
        super(symbol, name);
        this.currentPrice = validatePositiveNumber(currentPrice, "Ціна");
        this.marketCap = validatePositiveNumber(marketCap, "Ринкова капіталізація");
        this.volume24h = validatePositiveNumber(volume24h, "Обсяг торгів за 24 години");
        this.percentChange24h = percentChange24h;
        this.lastUpdated = validateLastUpdated(lastUpdated);
    }

    /**
     * Валідатор для перевірки додатніх числових значень.
     *
     * @param value     значення, що перевіряється.
     * @param fieldName назва поля для повідомлення про помилку.
     * @return перевірене значення.
     */
    private double validatePositiveNumber(double value, String fieldName) {
        if (value <= 0) {
            errors.add(fieldName + " повинно бути додатнім числом.");
        }
        return value;
    }

    /**
     * Валідатор для перевірки часу останнього оновлення.
     *
     * @param lastUpdated час останнього оновлення.
     * @return перевірений час.
     */
    private LocalDateTime validateLastUpdated(LocalDateTime lastUpdated) {
        if (lastUpdated == null || lastUpdated.isAfter(LocalDateTime.now())) {
            errors.add("Час останнього оновлення не може бути в майбутньому або пустим.");
        }
        return lastUpdated;
    }

    /**
     * Повертає символ криптовалюти.
     *
     * @return символ криптовалюти.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Повертає назву криптовалюти.
     *
     * @return назва криптовалюти.
     */
    public String getName() {
        return name;
    }

    /**
     * Повертає поточну ціну криптовалюти.
     *
     * @return поточна ціна.
     */
    public double getCurrentPrice() {
        return currentPrice;
    }

    /**
     * Повертає ринкову капіталізацію криптовалюти.
     *
     * @return ринкова капіталізація.
     */
    public double getMarketCap() {
        return marketCap;
    }

    /**
     * Повертає обсяг торгів криптовалюти за останні 24 години.
     *
     * @return обсяг торгів.
     */
    public double getVolume24h() {
        return volume24h;
    }

    /**
     * Повертає відсоткову зміну ціни криптовалюти за останні 24 години.
     *
     * @return відсоткова зміна.
     */
    public double getPercentChange24h() {
        return percentChange24h;
    }

    /**
     * Повертає час останнього оновлення інформації про криптовалюту.
     *
     * @return час останнього оновлення.
     */
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
