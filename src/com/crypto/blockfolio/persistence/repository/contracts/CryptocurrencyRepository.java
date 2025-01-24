package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;

public interface CryptocurrencyRepository extends Repository<Cryptocurrency, String> {

    Optional<Cryptocurrency> findBySymbol(String symbol);

    Optional<Cryptocurrency> findByName(String name);

    void update(Cryptocurrency cryptocurrency);

    void delete(String sybol);
}
