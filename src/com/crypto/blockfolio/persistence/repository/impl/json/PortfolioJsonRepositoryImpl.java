package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;

final class PortfolioJsonRepositoryImpl extends AbstractJsonRepository<Portfolio>
    implements PortfolioRepository {

    PortfolioJsonRepositoryImpl(Gson gson) {
        super(gson, JsonPathFactory.PORTFOLIOS_FILE.getPath(), TypeToken
            .getParameterized(Set.class, Portfolio.class)
            .getType());
    }

    @Override
    public Optional<Portfolio> findByName(String name) {
        return entities.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }
}
