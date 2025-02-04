package com.blockfolio.crypto.persistence.entity;

import com.blockfolio.crypto.persistence.Entity;
import java.math.BigDecimal;

public class PortfolioCoin {

    private BigDecimal balance;
    private BigDecimal averageBuyPrice;

    public PortfolioCoin(BigDecimal balance, BigDecimal averageBuyPrice) {
        this.balance = balance;
        this.averageBuyPrice = averageBuyPrice;
    }

    public void addToTheBalance(BigDecimal addToBalance) {

    }

    public void subtractFromTheBalance(BigDecimal subtractFromBalance) {
        
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getAverageBuyPrice() {
        return averageBuyPrice;
    }
}
