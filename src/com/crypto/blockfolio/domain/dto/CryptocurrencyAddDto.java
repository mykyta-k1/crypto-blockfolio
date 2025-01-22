package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.util.UUID;

public class CryptocurrencyAddDto extends Entity {

    private final String name;
    private final double currentPrice;
    private final double count;

    public CryptocurrencyAddDto(UUID id, String name, double currentPrice, double count) {
        super(id);
        this.name = validatedName(name);
        this.currentPrice = validatedPrice(currentPrice);
        this.count = validatedCount(count);
    }

    private String validatedName(String name) {
        ValidationUtils.validateRequired(name, "назва монети", errors);
        ValidationUtils.validateLength(name, 2, 50, "назва монети", errors);
        ValidationUtils.validatePattern(name, "^[a-zA-Z0-9\\s]+$", "назва монети", errors);
        return name;
    }

    private double validatedPrice(double price) {
        ValidationUtils.validatePositiveNumber(price, "ціна монети", errors);
        return price;
    }

    private double validatedCount(double count) {
        ValidationUtils.validatePositiveNumber(count, "кількість монет", errors);
        return count;
    }

    public String getName() {
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getCount() {
        return count;
    }
}
