package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реалізація репозиторію для роботи з криптовалютами у форматі JSON. Забезпечує збереження, пошук,
 * оновлення та фільтрацію об'єктів {@link Cryptocurrency}.
 */
public final class CryptocurrencyJsonRepositoryImpl
    extends GenericJsonRepository<Cryptocurrency, String>
    implements CryptocurrencyRepository {

    /**
     * Конструктор, який ініціалізує репозиторій криптовалют із вказаним об'єктом {@link Gson}.
     *
     * @param gson об'єкт для серіалізації та десеріалізації JSON.
     */
    public CryptocurrencyJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.CRYPTOCURRENCIES_FILE.getPath(),
            TypeToken.getParameterized(Set.class, Cryptocurrency.class).getType(),
            Cryptocurrency::getSymbol
        );
    }

    /**
     * Знаходить криптовалюту за її символом.
     *
     * @param symbol символ криптовалюти.
     * @return {@link Optional}, що містить криптовалюту, якщо її знайдено.
     */
    @Override
    public Optional<Cryptocurrency> findBySymbol(String symbol) {
        return entities.stream()
            .filter(crypto -> crypto.getSymbol().equalsIgnoreCase(symbol))
            .findFirst();
    }

    /**
     * Знаходить криптовалюту за її назвою.
     *
     * @param name назва криптовалюти.
     * @return {@link Optional}, що містить криптовалюту, якщо її знайдено.
     */
    @Override
    public Optional<Cryptocurrency> findByName(String name) {
        return entities.stream()
            .filter(crypto -> crypto.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    /**
     * Знаходить усі криптовалюти, ринкова капіталізація яких більша за вказане значення.
     *
     * @param marketCap значення ринкової капіталізації для фільтрації.
     * @return набір криптовалют, які відповідають критерію.
     */
    @Override
    public Set<Cryptocurrency> findAllByMarketCapGreaterThan(double marketCap) {
        return entities.stream()
            .filter(crypto -> crypto.getMarketCap() > marketCap)
            .collect(Collectors.toSet());
    }

    /**
     * Знаходить усі криптовалюти, обсяг торгів за 24 години яких більший за вказане значення.
     *
     * @param volume24h значення обсягу торгів за 24 години для фільтрації.
     * @return набір криптовалют, які відповідають критерію.
     */
    @Override
    public Set<Cryptocurrency> findAllByVolume24hGreaterThan(double volume24h) {
        return entities.stream()
            .filter(crypto -> crypto.getVolume24h() > volume24h)
            .collect(Collectors.toSet());
    }

    /**
     * Повертає всі криптовалюти, які є у репозиторії.
     *
     * @return набір усіх криптовалют.
     */
    @Override
    public Set<Cryptocurrency> findAll() {
        return new LinkedHashSet<>(entities);
    }

    /**
     * Оновлює інформацію про криптовалюту у репозиторії.
     *
     * @param cryptocurrency оновлена криптовалюта.
     * @throws IllegalArgumentException якщо криптовалюта з вказаним символом не знайдена.
     */
    @Override
    public void updateCryptocurrency(Cryptocurrency cryptocurrency) {
        Optional<Cryptocurrency> existingCrypto = findBySymbol(cryptocurrency.getSymbol());

        if (existingCrypto.isPresent()) {
            entities.remove(existingCrypto.get());
            entities.add(cryptocurrency);
            saveChanges();
        } else {
            throw new IllegalArgumentException(
                "Криптовалюта з символом " + cryptocurrency.getSymbol() + " не знайдена.");
        }
    }
}
