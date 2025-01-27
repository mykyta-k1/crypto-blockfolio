package com.crypto.blockfolio.persistence.exception;

/**
 * Виключення, що вказує на помилки введення/виведення під час роботи з JSON-файлами.
 * Використовується для обробки ситуацій, коли виникають проблеми з читанням або записом
 * JSON-файлів.
 */
public class JsonFileIOException extends RuntimeException {

    /**
     * Створює новий екземпляр {@link JsonFileIOException} із вказаним повідомленням про помилку.
     *
     * @param message текст помилки, що пояснює причину виключення.
     */
    public JsonFileIOException(String message) {
        super(message);
    }

    /**
     * Створює новий екземпляр {@link JsonFileIOException} із вказаним повідомленням і причиною.
     *
     * @param message текст помилки, що пояснює причину виключення.
     * @param cause   причина виключення (інше виключення).
     */
    public JsonFileIOException(String message, Throwable cause) {
        super(message, cause);
    }
}

