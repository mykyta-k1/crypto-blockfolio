package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;
import java.util.Set;

/**
 * Інтерфейс репозиторію для роботи з об'єктами {@link Cryptocurrency}. Забезпечує методи для
 * пошуку, оновлення та фільтрації криптовалют за різними критеріями.
 */
public interface CryptocurrencyRepository extends Repository<Cryptocurrency, String> {

    /**
     * Знаходить криптовалюту за її символом.
     *
     * @param symbol символ криптовалюти.
     * @return {@link Optional}, що містить криптовалюту, якщо її знайдено.
     */
    Optional<Cryptocurrency> findBySymbol(String symbol);

    /**
     * Знаходить криптовалюту за її назвою.
     *
     * @param name назва криптовалюти.
     * @return {@link Optional}, що містить криптовалюту, якщо її знайдено.
     */
    Optional<Cryptocurrency> findByName(String name);

    /**
     * Знаходить усі криптовалюти, ринкова капіталізація яких більша за вказане значення.
     *
     * @param marketCap значення ринкової капіталізації для фільтрації.
     * @return набір криптовалют, які відповідають критерію.
     */
    Set<Cryptocurrency> findAllByMarketCapGreaterThan(double marketCap);

    /**
     * Знаходить усі криптовалюти, обсяг торгів за 24 години яких більший за вказане значення.
     *
     * @param volume24h значення обсягу торгів за 24 години для фільтрації.
     * @return набір криптовалют, які відповідають критерію.
     */
    Set<Cryptocurrency> findAllByVolume24hGreaterThan(double volume24h);

    /**
     * Оновлює дані криптовалюти.
     *
     * @param cryptocurrency криптовалюта, яку потрібно оновити.
     */
    void updateCryptocurrency(Cryptocurrency cryptocurrency);
}

