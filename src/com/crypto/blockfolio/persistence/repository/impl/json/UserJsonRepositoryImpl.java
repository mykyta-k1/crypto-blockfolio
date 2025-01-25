package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class UserJsonRepositoryImpl extends GenericJsonRepository<User, UUID> implements
    UserRepository {

    public UserJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.USERS_FILE.getPath(),
            TypeToken.getParameterized(Set.class, User.class).getType(),
            User::getId
        );
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return entities.stream()
            .filter(
                user -> user.getUsername() != null && user.getUsername().equalsIgnoreCase(username))
            .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return entities.stream()
            .filter(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }

    @Override
    public void update(User user) {
        Optional<User> existingUser = findById(user.getId());

        if (existingUser.isPresent()) {
            entities.remove(existingUser.get());
            entities.add(user);
            saveChanges();
        } else {
            throw new IllegalArgumentException(
                "Користувача з ID " + user.getId() + " не знайдено.");
        }
    }

    @Override
    public void addPortfolio(UUID userId, UUID portfolioId) {
        findById(userId).ifPresentOrElse(user -> {
            if (user.getPortfolios().contains(portfolioId)) {
                throw new IllegalArgumentException("Це портфоліо вже належить користувачу.");
            }
            user.addPortfolio(portfolioId);
            saveChanges();
        }, () -> {
            throw new IllegalArgumentException("Користувача з ID " + userId + " не знайдено.");
        });
    }

    @Override
    public void removePortfolio(UUID userId, UUID portfolioId) {
        findById(userId).ifPresentOrElse(user -> {
            if (!user.getPortfolios().remove(portfolioId)) {
                throw new IllegalArgumentException("Це портфоліо не належить користувачу.");
            }
            saveChanges();
        }, () -> {
            throw new IllegalArgumentException("Користувача з ID " + userId + " не знайдено.");
        });
    }

    @Override
    public boolean ownsPortfolio(UUID userId, UUID portfolioId) {
        return findById(userId)
            .map(user -> user.getPortfolios().contains(portfolioId))
            .orElse(false);
    }

    @Override
    public Set<UUID> getPortfolios(UUID userId) {
        return findById(userId)
            .map(User::getPortfolios)
            .orElseThrow(
                () -> new IllegalArgumentException("Користувача з ID " + userId + " не знайдено."));
    }
}
