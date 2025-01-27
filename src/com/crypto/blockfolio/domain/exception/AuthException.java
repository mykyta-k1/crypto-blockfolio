package com.crypto.blockfolio.domain.exception;

/**
 * Виняток, що вказує на проблеми автентифікації. Використовується для обробки помилок, пов'язаних
 * із невірним логіном чи паролем.
 */
public class AuthException extends RuntimeException {

    /**
     * Створює новий екземпляр {@link AuthException} із вказаним повідомленням.
     *
     * @param message текст помилки, що пояснює причину виключення.
     */
    public AuthException(String message) {
        super(message);
    }

    /**
     * Створює новий екземпляр {@link AuthException} із повідомленням за замовчуванням. Повідомлення
     * за замовчуванням: "Не вірний логін чи пароль."
     */
    public AuthException() {
        super("Не вірний логін чи пароль.");
    }
}
