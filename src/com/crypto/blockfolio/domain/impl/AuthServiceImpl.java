package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.domain.exception.AuthException;
import com.crypto.blockfolio.domain.exception.UserAlreadyAuthException;
import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.crypto.blockfolio.persistence.repository.impl.json.AuthDataRepository;
import java.util.Optional;
import org.mindrot.bcrypt.BCrypt;

class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthDataRepository authDataRepository;
    private User user;

    AuthServiceImpl(UserRepository userRepository, AuthDataRepository authDataRepository) {
        this.userRepository = userRepository;
        this.authDataRepository = authDataRepository;
        loadAuthenticatedUser();
    }

    public boolean authenticate(String username, String password) {
        // Перевіряємо, чи вже існує аутентифікований користувач
        if (user != null) {
            throw new UserAlreadyAuthException("Ви вже авторизувалися як: %s"
                .formatted(user.getUsername()));
        }

        User foundedUser = userRepository.findByUsername(username)
            .orElseThrow(AuthException::new);

        if (!BCrypt.checkpw(password, foundedUser.getPassword())) {
            return false;
        }

        user = foundedUser;
        saveAuthenticatedUser();
        return true;
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    public User getUser() {
        return user;
    }

    public void logout() {
        if (user == null) {
            throw new UserAlreadyAuthException("Ви ще не автентифікавані.");
        }
        user = null;
    }

    /**
     * Saves the current authenticated user to the session repository.
     */
    private void saveAuthenticatedUser() {
        if (user != null) {
            authDataRepository.save(user.getUsername(), user.getPassword());
        }
    }

    /**
     * Loads the authenticated user from the session repository, if present.
     */
    private void loadAuthenticatedUser() {
        Optional<String[]> sessionData = authDataRepository.load();
        sessionData.ifPresent(data -> {
            String username = data[0];
            String hashedPassword = data[1];
            userRepository.findByUsername(username).ifPresentOrElse(storedUser -> {
                if (storedUser.getPassword().equals(hashedPassword)) {
                    user = storedUser;
                }
            }, () -> {
                // If the user in session data does not exist in the repository, clear the session
                authDataRepository.clear();
            });
        });
    }

    @Override
    public void updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Користувач не може бути null.");
        }
        userRepository.update(user);
        saveAuthenticatedUser();
    }

}
