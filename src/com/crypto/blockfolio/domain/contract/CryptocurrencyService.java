package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.Reportable;
import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import java.util.List;

public interface CryptocurrencyService extends Service<Cryptocurrency, String>,
    Reportable<Cryptocurrency> {

    Cryptocurrency getCryptocurrencyInfo(String symbol);

    List<Cryptocurrency> getAllCryptocurrencies();

}
