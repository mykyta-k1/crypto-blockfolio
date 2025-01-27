package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.Reportable;
import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.domain.dto.UserAddDto;
import com.crypto.blockfolio.persistence.entity.User;
import java.util.UUID;

/**
 * Інтерфейс UserService визначає сервіси для роботи з користувачами. Забезпечує отримання
 * користувачів за іменем чи електронною поштою, а також додавання нових користувачів.
 */
public interface UserService extends Service<User, UUID>, Reportable<User> {

    /**
     * Повертає користувача за його іменем користувача (username).
     *
     * @param username ім'я користувача.
     * @return користувач {@link User}, що відповідає вказаному імені.
     */
    User getByUsername(String username);

    /**
     * Повертає користувача за його електронною поштою (email).
     *
     * @param email електронна пошта користувача.
     * @return користувач {@link User}, що відповідає вказаній електронній пошті.
     */
    User getByEmail(String email);

    /**
     * Додає нового користувача на основі переданих даних.
     *
     * @param userAddDto об'єкт {@link UserAddDto}, що містить дані нового користувача.
     * @return створений користувач {@link User}.
     */
    User add(UserAddDto userAddDto);
}

