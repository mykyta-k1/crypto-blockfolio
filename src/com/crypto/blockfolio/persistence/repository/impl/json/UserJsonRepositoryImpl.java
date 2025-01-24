package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class UserJsonRepositoryImpl extends AbstractJsonRepository<User, UUID>
    implements UserRepository {

    UserJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.USERS_FILE.getPath(),
            TypeToken.getParameterized(Set.class, User.class).getType(),
            User::getId // Вказуємо, що ідентифікатором є UUID
        );
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return entities.stream()
            .filter(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(username))
            .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return entities.stream()
            .filter(u -> u.getEmail() != null && u.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }

    @Override
    public void update(User user) {
        // Перевірка валідності нового користувача
        if (!user.isValid()) {
            throw new IllegalArgumentException(
                "Користувач має невалідні дані: " + user.getErrors());
        }

        // Пошук існуючого користувача
        Optional<User> existingUser = findById(user.getId());

        // Оновлення або повідомлення про відсутність
        if (existingUser.isPresent()) {
            entities.remove(existingUser.get());
            entities.add(user);
            saveChanges();
            System.out.println("Користувач із ID " + user.getId() + " успішно оновлений.");
        } else {
            throw new RuntimeException("Користувач із ID " + user.getId() + " не знайдений.");
        }
    }


    @Override
    public void addPortfolio(UUID userId, UUID portfolioId) {
        User user = findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено."));

        if (user.getPortfolios().contains(portfolioId)) {
            throw new IllegalArgumentException("Це портфоліо вже належить користувачу.");
        }

        user.getPortfolios().add(portfolioId);
        add(user); // Оновлюємо користувача в репозиторії
    }

    @Override
    public void removePortfolio(UUID userId, UUID portfolioId) {
        User user = findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено."));

        if (!user.getPortfolios().remove(portfolioId)) {
            throw new IllegalArgumentException("Це портфоліо не належить користувачу.");
        }

        add(user); // Оновлюємо користувача в репозиторії
    }

    @Override
    public boolean ownsPortfolio(UUID userId, UUID portfolioId) {
        User user = findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено."));

        return user.getPortfolios().contains(portfolioId);
    }

    @Override
    public Set<UUID> getPortfolios(UUID userId) {
        User user = findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено."));

        return user.getPortfolios();
    }

}
