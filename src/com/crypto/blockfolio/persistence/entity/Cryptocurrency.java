package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.CryptoEntity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import java.time.LocalDateTime;

/**
 * Клас, що представляє криптовалюту як сутність із ринковими показниками. Використовується для
 * відображення поточної інформації про криптовалюту, такої як ціна, ринкова капіталізація, обсяг
 * торгів та зміна відсотків за останні 24 години.
 */
public class Cryptocurrency extends CryptoEntity implements Comparable<Cryptocurrency> {

    /**
     * Поточна ціна криптовалюти.
     */
    private double currentPrice;
    /**
     * Ринкова капіталізація криптовалюти.
     */
    private double marketCap;
    /**
     * Обсяг торгів криптовалюти за останні 24 години.
     */
    private double volume24h;
    /**
     * Відсоткова зміна ціни криптовалюти за останні 24 години.
     */
    private double percentChange24h;
    /**
     * Час останнього оновлення даних про криптовалюту.
     */
    private LocalDateTime lastUpdated;

    /**
     * Конструктор для створення нового екземпляра {@link Cryptocurrency}.
     *
     * @param symbol           символ криптовалюти (наприклад, BTC, ETH).
     * @param name             назва криптовалюти.
     * @param currentPrice     поточна ціна криптовалюти.
     * @param marketCap        ринкова капіталізація криптовалюти.
     * @param volume24h        обсяг торгів за останні 24 години.
     * @param percentChange24h відсоткова зміна ціни за останні 24 години.
     * @param lastUpdated      час останнього оновлення даних.
     */
    public Cryptocurrency(String symbol, String name, double currentPrice, double marketCap,
        double volume24h, double percentChange24h, LocalDateTime lastUpdated) {
        super(symbol, name);
        this.currentPrice = validatePositiveNumber(currentPrice, "Ціна");
        this.marketCap = validatePositiveNumber(marketCap, "Ринкова капіталізація");
        this.volume24h = validatePositiveNumber(volume24h, "Обсяг торгів за 24 години");
        this.percentChange24h = percentChange24h;
        this.lastUpdated = validateLastUpdated(lastUpdated);

        if (!isValid()) {
            throw new EntityArgumentException(errors);
        }
    }

    /**
     * Перевіряє, чи є значення додатнім числом.
     *
     * @param value     значення для перевірки.
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
     * Перевіряє та повертає коректний час останнього оновлення.
     *
     * @param lastUpdated час для перевірки.
     * @return перевірений час.
     */
    private LocalDateTime validateLastUpdated(LocalDateTime lastUpdated) {
        if (lastUpdated == null || lastUpdated.isAfter(LocalDateTime.now())) {
            errors.add("Час останнього оновлення не може бути в майбутньому або пустим.");
        }
        return lastUpdated;
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
     * Встановлює поточну ціну криптовалюти.
     *
     * @param currentPrice нова поточна ціна.
     */
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = validatePositiveNumber(currentPrice, "Ціна");
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
     * Встановлює ринкову капіталізацію криптовалюти.
     *
     * @param marketCap нова ринкова капіталізація.
     */
    public void setMarketCap(double marketCap) {
        this.marketCap = validatePositiveNumber(marketCap, "Ринкова капіталізація");
    }

    /**
     * Повертає обсяг торгів за останні 24 години.
     *
     * @return обсяг торгів.
     */
    public double getVolume24h() {
        return volume24h;
    }

    /**
     * Встановлює обсяг торгів за останні 24 години.
     *
     * @param volume24h новий обсяг торгів.
     */
    public void setVolume24h(double volume24h) {
        this.volume24h = validatePositiveNumber(volume24h, "Обсяг торгів за 24 години");
    }

    /**
     * Повертає відсоткову зміну ціни за останні 24 години.
     *
     * @return відсоткова зміна.
     */
    public double getPercentChange24h() {
        return percentChange24h;
    }

    /**
     * Встановлює відсоткову зміну ціни за останні 24 години.
     *
     * @param percentChange24h нова відсоткова зміна.
     */
    public void setPercentChange24h(double percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

    /**
     * Повертає час останнього оновлення даних про криптовалюту.
     *
     * @return час останнього оновлення.
     */
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Встановлює час останнього оновлення даних.
     *
     * @param lastUpdated новий час оновлення.
     */
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = validateLastUpdated(lastUpdated);
    }


    /**
     * Порівнює поточну криптовалюту з іншою за символом.
     *
     * @param o інша криптовалюта.
     * @return результат порівняння.
     */
    @Override
    public int compareTo(Cryptocurrency o) {
        return this.getSymbol().compareTo(o.getSymbol());
    }

    /**
     * Повертає строкове представлення криптовалюти.
     *
     * @return строкове представлення.
     */
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
