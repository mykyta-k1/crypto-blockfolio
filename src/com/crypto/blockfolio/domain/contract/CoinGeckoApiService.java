package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import java.util.List;

public interface CoinGeckoApiService {

    Cryptocurrency getCryptocurrencyInfo(String name);

    List<Cryptocurrency> getAllCryptocurrencies();

}
