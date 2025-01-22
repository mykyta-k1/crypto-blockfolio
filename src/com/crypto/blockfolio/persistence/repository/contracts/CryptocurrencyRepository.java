package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;

public interface CryptocurrencyRepository extends Repository<Cryptocurrency> {

    Optional<Cryptocurrency> findByName(String name);

    Cryptocurrency update(Cryptocurrency cryptocurrency);
}
