package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.persistence.entity.User;

/**
 * Інтерфейс AuthService визначає контракти для управління автентифікацією користувачів у системі.
 * Забезпечує методи для входу, перевірки автентифікації, отримання інформації про користувача та
 * виходу.
 */
public interface AuthService {

    /**
     * Виконує автентифікацію користувача на основі імені користувача та пароля.
     *
     * @param username ім'я користувача.
     * @param password пароль користувача.
     * @return true, якщо автентифікація успішна, інакше false.
     */
    boolean authenticate(String username, String password);

    /**
     * Перевіряє, чи автентифікований користувач у поточній сесії.
     *
     * @return true, якщо користувач автентифікований, інакше false.
     */
    boolean isAuthenticated();

    /**
     * Повертає об'єкт користувача, автентифікованого у поточній сесії.
     *
     * @return об'єкт {@link User}, якщо користувач автентифікований, інакше null.
     */
    User getUser();

    /**
     * Завершує автентифікацію користувача, видаляючи його сесію.
     */
    void logout();

    /**
     * Оновлює дані користувача у системі.
     *
     * @param user оновлений об'єкт {@link User}.
     */
    void updateUser(User user);
}

