package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;
import java.util.Set;

public interface CryptocurrencyRepository extends Repository<Cryptocurrency, String> {

    Optional<Cryptocurrency> findBySymbol(String symbol);

    Optional<Cryptocurrency> findByName(String name);

    Set<Cryptocurrency> findAllByMarketCapGreaterThan(double marketCap);

    Set<Cryptocurrency> findAllByVolume24hGreaterThan(double volume24h);

    void updateCryptocurrency(Cryptocurrency cryptocurrency);
    
}
