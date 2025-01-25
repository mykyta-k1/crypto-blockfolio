package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserRepository extends Repository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void update(User user);

    void addPortfolio(UUID userId, UUID portfolioId);

    void removePortfolio(UUID userId, UUID portfolioId);

    boolean ownsPortfolio(UUID userId, UUID portfolioId);

    Set<UUID> getPortfolios(UUID userId);
}


