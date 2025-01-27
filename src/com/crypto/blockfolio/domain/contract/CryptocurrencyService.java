package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.Reportable;
import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.domain.dto.CryptocurrencyAddDto;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import java.util.List;

/**
 * Інтерфейс CryptocurrencyService визначає сервіси для управління криптовалютами. Наслідує
 * {@link Service} для базових CRUD-операцій та {@link Reportable} для створення звітів.
 */
public interface CryptocurrencyService extends Service<Cryptocurrency, String>,
    Reportable<Cryptocurrency> {

    /**
     * Отримує інформацію про криптовалюту за її символом.
     *
     * @param symbol символ криптовалюти (наприклад, "BTC" для Bitcoin).
     * @return об'єкт {@link Cryptocurrency}, що містить інформацію про криптовалюту.
     */
    Cryptocurrency getCryptocurrencyInfo(String symbol);

    /**
     * Отримує список усіх доступних криптовалют.
     *
     * @return список об'єктів {@link Cryptocurrency}.
     */
    List<Cryptocurrency> getAllCryptocurrencies();

    /**
     * Додає нову криптовалюту на основі переданих даних.
     *
     * @param cryptocurrencyDto об'єкт {@link CryptocurrencyAddDto}, що містить дані нової
     *                          криптовалюти.
     */
    void addCryptocurrency(CryptocurrencyAddDto cryptocurrencyDto);
}

