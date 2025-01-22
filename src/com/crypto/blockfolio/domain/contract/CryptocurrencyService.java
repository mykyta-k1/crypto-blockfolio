package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.domain.dto.CryptocurrencyAddDto;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;

public interface CryptocurrencyService extends Service<Cryptocurrency> {

    void syncCryptocurrencyPrice(String name);

    Cryptocurrency addCryptocurrency(CryptocurrencyAddDto cryptocurrencyAddDto);
}

