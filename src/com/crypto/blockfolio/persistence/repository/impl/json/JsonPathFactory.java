package com.crypto.blockfolio.persistence.repository.impl.json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Перелічення для управління шляхами до JSON-файлів у системі. Визначає назви файлів для зберігання
 * даних і забезпечує їх повний шлях.
 */
enum JsonPathFactory {

    /**
     * Файл з даними користувачів.
     */
    USERS_FILE("users.json"),

    /**
     * Файл з даними портфелів.
     */
    PORTFOLIOS_FILE("portfolios.json"),

    /**
     * Файл з даними транзакцій.
     */
    TRANSACTIONS_FILE("transactions.json"),

    /**
     * Файл з даними криптовалют.
     */
    CRYPTOCURRENCIES_FILE("cryptocurrencies.json");

    /**
     * Базова директорія для зберігання файлів даних.
     */
    private static final String DATA_DIRECTORY = "data";

    /**
     * Ім'я файлу.
     */
    private final String fileName;

    /**
     * Конструктор для встановлення імені файлу.
     *
     * @param fileName ім'я файлу.
     */
    JsonPathFactory(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Повертає повний шлях до файлу.
     *
     * @return об'єкт {@link Path}, що представляє шлях до файлу.
     */
    public Path getPath() {
        Path directory = Path.of(DATA_DIRECTORY);

        if (Files.notExists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException("Не вдалося створити директорію: " + DATA_DIRECTORY, e);
            }
        }

        return directory.resolve(this.fileName);
    }
}

