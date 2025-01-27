package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.exception.JsonFileIOException;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Репозиторій для роботи з файлами аутентифікаційних даних у форматі JSON. Забезпечує збереження,
 * завантаження та очищення даних аутентифікації.
 */
public class AuthDataRepository {

    /**
     * Шлях до файлу з даними аутентифікації.
     */
    private static final Path AUTH_DATA_FILE = Path.of("data", "auth_data.json");

    /**
     * Об'єкт для серіалізації та десеріалізації JSON.
     */
    private final Gson gson;

    /**
     * Конструктор, який ініціалізує репозиторій і перевіряє існування файлу аутентифікації.
     */
    public AuthDataRepository() {
        this.gson = new Gson();
        initializeFile();
    }

    /**
     * Зберігає дані аутентифікації у файл.
     *
     * @param username     ім'я користувача.
     * @param passwordHash хешований пароль.
     * @throws JsonFileIOException у разі помилки запису у файл.
     */
    public void save(String username, String passwordHash) {
        try (FileWriter writer = new FileWriter(AUTH_DATA_FILE.toFile())) {
            gson.toJson(new String[]{username, passwordHash}, writer);
        } catch (IOException e) {
            throw new JsonFileIOException("Failed to save auth data: " + e.getMessage());
        }
    }

    /**
     * Завантажує дані аутентифікації з файлу.
     *
     * @return {@link Optional}, що містить масив з іменем користувача і хешованим паролем, або
     * {@code Optional.empty()}, якщо файл не існує чи порожній.
     * @throws JsonFileIOException у разі помилки читання файлу.
     */
    public Optional<String[]> load() {
        if (!Files.exists(AUTH_DATA_FILE)) {
            return Optional.empty();
        }

        try (FileReader reader = new FileReader(AUTH_DATA_FILE.toFile())) {
            return Optional.ofNullable(gson.fromJson(reader, String[].class));
        } catch (IOException e) {
            throw new JsonFileIOException("Failed to load auth data: " + e.getMessage());
        }
    }

    /**
     * Очищає файл даних аутентифікації.
     *
     * @throws JsonFileIOException у разі помилки видалення файлу.
     */
    public void clear() {
        try {
            Files.deleteIfExists(AUTH_DATA_FILE);
        } catch (IOException e) {
            throw new JsonFileIOException("Failed to clear auth data: " + e.getMessage());
        }
    }

    /**
     * Ініціалізує файл аутентифікації, створюючи його, якщо він не існує.
     *
     * @throws JsonFileIOException у разі помилки створення файлу.
     */
    private void initializeFile() {
        try {
            if (!Files.exists(AUTH_DATA_FILE)) {
                Files.createDirectories(AUTH_DATA_FILE.getParent());
                Files.createFile(AUTH_DATA_FILE);
            }
        } catch (IOException e) {
            throw new JsonFileIOException("Failed to initialize auth data file: " + e.getMessage());
        }
    }
}
