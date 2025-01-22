package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;

public interface UserRepository extends Repository<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    void update(User user);
}
