package com.blockfolio.crypto.persistence.entity;

import com.blockfolio.crypto.domain.utils.TimeUtils;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Candle {

    private LocalDate timestamp;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;

    public Candle(long timestamp, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close) {
        this.timestamp = TimeUtils.getDateFromTimestamp(timestamp);
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getClose() {
        return close;
    }
}
