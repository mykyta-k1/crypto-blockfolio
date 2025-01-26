package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.persistence.entity.User;

/**
 * Кон
 */
public interface AuthService {

    boolean authenticate(String username, String password);

    boolean isAuthenticated();

    User getUser();

    void logout();

    void updateUser(User user);
}
