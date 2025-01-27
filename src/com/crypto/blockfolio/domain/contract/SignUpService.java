package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.dto.UserAddDto;
import java.util.function.Supplier;

/**
 * Інтерфейс SignUpService визначає сервіси для реєстрації користувачів.
 */
public interface SignUpService {

    /**
     * Реєструє нового користувача на основі переданих даних.
     *
     * @param userAddDto       об'єкт {@link UserAddDto}, що містить дані нового користувача.
     * @param waitForUserInput функціональний інтерфейс {@link Supplier}, який забезпечує введення
     *                         користувача (наприклад, для підтвердження або додаткової
     *                         інформації).
     */
    void signUp(UserAddDto userAddDto, Supplier<String> waitForUserInput);

    /**
     * Перевіряє, чи існує користувач із заданими іменем користувача або електронною поштою.
     *
     * @param username ім'я користувача для перевірки.
     * @param email    електронна пошта для перевірки.
     * @return {@code true}, якщо користувач із заданими параметрами вже існує, інакше
     * {@code false}.
     */
    boolean userExists(String username, String email);
}

