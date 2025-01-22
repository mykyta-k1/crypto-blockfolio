package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;

final class CryptocurrencyJsonRepositoryImpl extends AbstractJsonRepository<Cryptocurrency>
    implements CryptocurrencyRepository {

    CryptocurrencyJsonRepositoryImpl(Gson gson) {
        super(gson, JsonPathFactory.CRYPTOCURRENCY_FILE.getPath(), TypeToken
            .getParameterized(Set.class, Cryptocurrency.class)
            .getType());
    }

    @Override
    public Optional<Cryptocurrency> findByName(String name) {
        return entities.stream().filter(c -> c.getName().equalsIgnoreCase(name.toLowerCase()))
            .findFirst();
    }

    @Override
    public Cryptocurrency update(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || cryptocurrency.getId() == null) {
            throw new IllegalArgumentException("Криптовалюта або її ID не може бути null.");
        }

        Optional<Cryptocurrency> existingEntity = findById(cryptocurrency.getId());

        if (existingEntity.isPresent()) {
            Cryptocurrency entity = existingEntity.get();

            // Оновлюємо значення
            entity.setName(cryptocurrency.getName());
            entity.setCurrentPrice(cryptocurrency.getCurrentPrice());
            entity.setCount(cryptocurrency.getCount());

            // Синхронізуємо зміни в JSON-файлі
            saveChanges();
            return entity;
        } else {
            throw new RuntimeException(
                "Криптовалюта з ID %s не знайдена".formatted(cryptocurrency.getId()));
        }
    }

}
