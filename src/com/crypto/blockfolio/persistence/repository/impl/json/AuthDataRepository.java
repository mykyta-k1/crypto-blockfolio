package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.exception.JsonFileIOException;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class AuthDataRepository {

    private static final Path AUTH_DATA_FILE = Path.of("data", "auth_data.json");
    private final Gson gson;

    public AuthDataRepository() {
        this.gson = new Gson();
        initializeFile();
    }

    /**
     * Saves the authentication data to the file.
     *
     * @param username     The username.
     * @param passwordHash The hashed password.
     */
    public void save(String username, String passwordHash) {
        try (FileWriter writer = new FileWriter(AUTH_DATA_FILE.toFile())) {
            gson.toJson(new String[]{username, passwordHash}, writer);
        } catch (IOException e) {
            throw new JsonFileIOException("Failed to save auth data: " + e.getMessage());
        }
    }

    /**
     * Loads authentication data from the file.
     *
     * @return An Optional containing username and password hash, or empty if not available.
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
     * Clears the authentication data file.
     */
    public void clear() {
        try {
            Files.deleteIfExists(AUTH_DATA_FILE);
        } catch (IOException e) {
            throw new JsonFileIOException("Failed to clear auth data: " + e.getMessage());
        }
    }

    /**
     * Initializes the auth data file if it does not exist.
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
