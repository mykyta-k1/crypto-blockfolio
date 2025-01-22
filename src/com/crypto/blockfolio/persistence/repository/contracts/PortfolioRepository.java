package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.Portfolio;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;

public interface PortfolioRepository extends Repository<Portfolio> {

    Optional<Portfolio> findByName(String name);
}
