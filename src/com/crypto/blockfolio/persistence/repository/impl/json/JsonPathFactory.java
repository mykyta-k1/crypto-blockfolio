package com.crypto.blockfolio.persistence.repository.impl.json;

import java.nio.file.Path;

enum JsonPathFactory {

    USERS_FILE("users.json"),
    PORTFOLIOS_FILE("portfolios.json"),
    TRANSACTIONS_FILE("transactions.json");

    private static final String DATA_DIRECTORY = "data";
    private final String fileName;

    JsonPathFactory(String fileName) {
        this.fileName = fileName;
    }

    public Path getPath() {
        return Path.of(DATA_DIRECTORY, this.fileName);
    }
}
