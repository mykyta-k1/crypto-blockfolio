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
        super(gson, JsonPathFactory.USERS_FILE.getPath(), TypeToken
            .getParameterized(Set.class, Cryptocurrency.class)
            .getType());
    }

    @Override
    public Optional<Cryptocurrency> findByName(String name) {
        return entities.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
    }
}
