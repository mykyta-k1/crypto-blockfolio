package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Реалізація репозиторію для роботи з користувачами у форматі JSON. Забезпечує збереження, пошук,
 * оновлення та управління портфелями користувачів.
 */
public final class UserJsonRepositoryImpl extends GenericJsonRepository<User, UUID> implements
    UserRepository {

    /**
     * Конструктор для ініціалізації репозиторію користувачів.
     *
     * @param gson об'єкт для серіалізації та десеріалізації JSON.
     */
    public UserJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.USERS_FILE.getPath(),
            TypeToken.getParameterized(Set.class, User.class).getType(),
            User::getId
        );
    }

    /**
     * Знаходить користувача за логіном.
     *
     * @param username логін користувача.
     * @return {@link Optional}, що містить користувача, якщо його знайдено.
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return entities.stream()
            .filter(
                user -> user.getUsername() != null && user.getUsername().equalsIgnoreCase(username))
            .findFirst();
    }

    /**
     * Знаходить користувача за електронною поштою.
     *
     * @param email електронна пошта користувача.
     * @return {@link Optional}, що містить користувача, якщо його знайдено.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return entities.stream()
            .filter(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }

    /**
     * Оновлює інформацію про користувача.
     *
     * @param user користувач для оновлення.
     * @throws IllegalArgumentException якщо користувач не знайдений.
     */
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

    /**
     * Додає портфель до користувача.
     *
     * @param userId      ідентифікатор користувача.
     * @param portfolioId ідентифікатор портфеля.
     * @throws IllegalArgumentException якщо користувача не знайдено або портфель вже додано.
     */
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

    /**
     * Видаляє портфель з користувача.
     *
     * @param userId      ідентифікатор користувача.
     * @param portfolioId ідентифікатор портфеля.
     * @throws IllegalArgumentException якщо користувача не знайдено або портфель не належить
     *                                  користувачу.
     */
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

    /**
     * Перевіряє, чи належить портфель користувачу.
     *
     * @param userId      ідентифікатор користувача.
     * @param portfolioId ідентифікатор портфеля.
     * @return {@code true}, якщо портфель належить користувачу, інакше {@code false}.
     */
    @Override
    public boolean ownsPortfolio(UUID userId, UUID portfolioId) {
        return findById(userId)
            .map(user -> user.getPortfolios().contains(portfolioId))
            .orElse(false);
    }

    /**
     * Повертає список портфелів, пов'язаних із користувачем.
     *
     * @param userId ідентифікатор користувача.
     * @return набір ідентифікаторів портфелів.
     * @throws IllegalArgumentException якщо користувача не знайдено.
     */
    @Override
    public Set<UUID> getPortfolios(UUID userId) {
        return findById(userId)
            .map(User::getPortfolios)
            .orElseThrow(
                () -> new IllegalArgumentException("Користувача з ID " + userId + " не знайдено."));
    }
}
