package com.crypto.blockfolio.persistence.repository.contracts;

import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.Repository;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Інтерфейс репозиторію для роботи з об'єктами {@link User}. Забезпечує методи для пошуку,
 * оновлення та управління портфелями, пов'язаними з користувачами.
 */
public interface UserRepository extends Repository<User, UUID> {

    /**
     * Знаходить користувача за його логіном.
     *
     * @param username логін користувача.
     * @return {@link Optional}, що містить користувача, якщо його знайдено.
     */
    Optional<User> findByUsername(String username);

    /**
     * Знаходить користувача за його електронною поштою.
     *
     * @param email електронна пошта користувача.
     * @return {@link Optional}, що містить користувача, якщо його знайдено.
     */
    Optional<User> findByEmail(String email);

    /**
     * Оновлює дані користувача.
     *
     * @param user користувач, якого потрібно оновити.
     */
    void update(User user);

    /**
     * Додає портфель до користувача.
     *
     * @param userId      ідентифікатор користувача.
     * @param portfolioId ідентифікатор портфеля.
     */
    void addPortfolio(UUID userId, UUID portfolioId);

    /**
     * Видаляє портфель з користувача.
     *
     * @param userId      ідентифікатор користувача.
     * @param portfolioId ідентифікатор портфеля.
     */
    void removePortfolio(UUID userId, UUID portfolioId);

    /**
     * Перевіряє, чи належить портфель користувачу.
     *
     * @param userId      ідентифікатор користувача.
     * @param portfolioId ідентифікатор портфеля.
     * @return {@code true}, якщо портфель належить користувачу, інакше {@code false}.
     */
    boolean ownsPortfolio(UUID userId, UUID portfolioId);

    /**
     * Повертає список портфелів, пов'язаних із користувачем.
     *
     * @param userId ідентифікатор користувача.
     * @return набір ідентифікаторів портфелів.
     */
    Set<UUID> getPortfolios(UUID userId);
}



