package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import java.util.List;

/**
 * Інтерфейс CoinGeckoApiService визначає методи для взаємодії з API CoinGecko. Забезпечує отримання
 * інформації про криптовалюту та список усіх криптовалют.
 */
public interface CoinGeckoApiService {

    /**
     * Отримує детальну інформацію про криптовалюту за її назвою.
     *
     * @param name назва криптовалюти (наприклад, "Bitcoin").
     * @return об'єкт {@link Cryptocurrency}, що містить інформацію про криптовалюту.
     */
    Cryptocurrency getCryptocurrencyInfo(String name);

    /**
     * Отримує список усіх доступних криптовалют.
     *
     * @return список об'єктів {@link Cryptocurrency}.
     */
    List<Cryptocurrency> getAllCryptocurrencies();
}

