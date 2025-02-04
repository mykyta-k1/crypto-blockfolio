package com.blockfolio.crypto.domain.contract;

import com.blockfolio.crypto.domain.dto.CandleDto;
import com.blockfolio.crypto.domain.dto.CoinDetailsDto;
import java.util.List;

public interface CoinGeckoApiService {

    CoinDetailsDto getCoinDetails(String coinId);

    List<CandleDto> getCandleBy30DaysOnCoin(String coinId);

    List<CoinDetailsDto> searchCoinByNameOrId(String query);

    List<CoinDetailsDto> getCoinWithPage(int perPage, int page);
}
