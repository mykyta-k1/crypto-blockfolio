package com.blockfolio.crypto.persistence.entity;

import com.blockfolio.crypto.domain.dto.CandleDto;
import com.blockfolio.crypto.domain.dto.CoinDetailsDto;
import java.util.List;

public class Chart {

    private String coinId;
    private List<CandleDto> dayCandle;

    public Chart(String coinId, List<CandleDto> dayCandle) {
        this.coinId = coinId;
        this.dayCandle = dayCandle;
    }

    public String getCoinId() {
        return coinId;
    }

    public void setCoinId(String coinId) {
        this.coinId = coinId;
    }

    public List<CandleDto> getDayCandle() {
        return dayCandle;
    }

    public void setDayCandle(List<CandleDto> dayCandle) {
        this.dayCandle = dayCandle;
    }
}
