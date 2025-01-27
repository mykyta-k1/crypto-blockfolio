package com.crypto.blockfolio.domain.exception;

/**
 * Виняток, що вказує на відсутність сутності у базі даних або в системі. Використовується для
 * ситуацій, коли запитувана сутність не може бути знайдена.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Створює новий екземпляр {@link EntityNotFoundException} із вказаним повідомленням.
     *
     * @param message текст помилки, що пояснює причину виключення.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}

