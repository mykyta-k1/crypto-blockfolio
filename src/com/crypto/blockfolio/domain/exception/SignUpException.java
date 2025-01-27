package com.crypto.blockfolio.domain.exception;

/**
 * Виняток, що вказує на помилку під час реєстрації користувача. Використовується для обробки
 * помилок, пов'язаних із невірними даними або іншими проблемами реєстрації.
 */
public class SignUpException extends RuntimeException {

    /**
     * Створює новий екземпляр {@link SignUpException} із вказаним повідомленням.
     *
     * @param message текст помилки, що пояснює причину виключення.
     */
    public SignUpException(String message) {
        super(message);
    }
}

