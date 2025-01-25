package com.crypto.blockfolio.persistence.repository;

import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.crypto.blockfolio.persistence.repository.impl.json.AuthDataRepository;
import com.crypto.blockfolio.persistence.repository.impl.json.JsonRepositoryFactory;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public abstract class RepositoryFactory {

    public static final int JSON = 1;
    public static final int XML = 2;
    public static final int POSTGRESQL = 3;

    public static RepositoryFactory getRepositoryFactory(int whichFactory)
        throws NotImplementedException {
        return switch (whichFactory) {
            case JSON -> JsonRepositoryFactory.getInstance();
            case XML -> throw new NotImplementedException("Робота з XML файлами не реалізована.");
            case POSTGRESQL -> throw new NotImplementedException(
                "Робота з СУБД PostgreSQL не реалізована.");
            default -> throw new IllegalArgumentException(
                "Помилка при виборі фабрики репозиторіїв.");
        };
    }

    public abstract UserRepository getUserRepository();

    public abstract CryptocurrencyRepository getCryptocurrencyRepository();

    public abstract PortfolioRepository getPortfolioRepository();

    public abstract TransactionRepository getTransactionRepository();

    public abstract AuthDataRepository getAuthDataRepository();

    public abstract void commit();
}
