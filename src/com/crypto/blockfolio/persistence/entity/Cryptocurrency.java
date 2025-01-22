package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Cryptocurrency extends Entity implements Comparable<Cryptocurrency> {

    private BigDecimal balance;
    private double currentPrice;
    private String name;
    private double count;

    public Cryptocurrency(UUID id, String name, double currentPrice) {
        super(id);
        setName(name);
        setCurrentPrice(currentPrice);
        calculateBalance();

        if (!this.isValid()) {
            throw new EntityArgumentException(errors);
        }
    }

    @Override
    public int compareTo(Cryptocurrency o) {
        if (o == null || o.name == null) {
            return 1;
        }
        return this.name.compareTo(o.name);
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        if (currentPrice <= 0) {
            errors.add("Ціна монети повинна бути додатною.");
        }

        this.currentPrice = currentPrice;
        calculateBalance();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        final String templateName = "назва монети";
        name = name != null ? name.trim() : null;
        ValidationUtils.validateRequired(name, templateName, errors);
        ValidationUtils.validateLength(name, 2, 50, templateName, errors);
        ValidationUtils.validatePattern(name, "^[a-zA-Z0-9\\s]+$", templateName, errors);
        this.name = name;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        final String templateName = "кількість монет";
        ValidationUtils.validatePositiveNumber(count, templateName, errors);
        this.count = count;
        calculateBalance();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    private void calculateBalance() {
        if (currentPrice <= 0 || count <= 0) {
            this.balance = BigDecimal.ZERO;
        } else {
            this.balance = BigDecimal.valueOf(currentPrice)
                .multiply(BigDecimal.valueOf(count))
                .setScale(2, RoundingMode.HALF_UP);
        }
    }

    @Override
    public String toString() {
        return "Cryptocurrency{" +
            "name='" + name + '\'' +
            ", currentPrice=" + currentPrice +
            ", count=" + count +
            ", balance=" + balance +
            '}';
    }
}
