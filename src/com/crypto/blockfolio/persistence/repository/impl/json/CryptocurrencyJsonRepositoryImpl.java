package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class CryptocurrencyJsonRepositoryImpl
    extends GenericJsonRepository<Cryptocurrency, String>
    implements CryptocurrencyRepository {

    public CryptocurrencyJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.CRYPTOCURRENCIES_FILE.getPath(),
            TypeToken.getParameterized(Set.class, Cryptocurrency.class).getType(),
            Cryptocurrency::getSymbol // Символ криптовалюти використовується як ідентифікатор
        );
    }

    @Override
    public Optional<Cryptocurrency> findBySymbol(String symbol) {
        return entities.stream()
            .filter(crypto -> crypto.getSymbol().equalsIgnoreCase(symbol))
            .findFirst();
    }

    @Override
    public Optional<Cryptocurrency> findByName(String name) {
        return entities.stream()
            .filter(crypto -> crypto.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    @Override
    public Set<Cryptocurrency> findAllByMarketCapGreaterThan(double marketCap) {
        return entities.stream()
            .filter(crypto -> crypto.getMarketCap() > marketCap)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<Cryptocurrency> findAllByVolume24hGreaterThan(double volume24h) {
        return entities.stream()
            .filter(crypto -> crypto.getVolume24h() > volume24h)
            .collect(Collectors.toSet());
    }

    @Override
    public Set<Cryptocurrency> findAll() {
        return new LinkedHashSet<>(entities);
    }


    @Override
    public void updateCryptocurrency(Cryptocurrency cryptocurrency) {
        // Знаходимо криптовалюту, яку потрібно оновити
        Optional<Cryptocurrency> existingCrypto = findBySymbol(cryptocurrency.getSymbol());

        if (existingCrypto.isPresent()) {
            // Видаляємо стару версію
            entities.remove(existingCrypto.get());
            // Додаємо оновлену версію
            entities.add(cryptocurrency);
            // Зберігаємо зміни
            saveChanges();
        } else {
            throw new IllegalArgumentException(
                "Криптовалюта з символом " + cryptocurrency.getSymbol() + " не знайдена.");
        }
    }
}
