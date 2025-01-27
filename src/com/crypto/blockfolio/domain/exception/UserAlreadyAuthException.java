package com.crypto.blockfolio.domain.exception;

/**
 * Виняток, що вказує на спробу автентифікувати користувача, який вже увійшов у систему.
 * Використовується для обробки випадків повторної автентифікації.
 */
public class UserAlreadyAuthException extends RuntimeException {

    /**
     * Створює новий екземпляр {@link UserAlreadyAuthException} із вказаним повідомленням.
     *
     * @param message текст помилки, що пояснює причину виключення.
     */
    public UserAlreadyAuthException(String message) {
        super(message);
    }
}

