package com.crypto.blockfolio.persistence.repository;

import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.crypto.blockfolio.persistence.repository.impl.json.AuthDataRepository;
import com.crypto.blockfolio.persistence.repository.impl.json.JsonRepositoryFactory;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * Абстрактна фабрика для створення різних типів репозиторіїв. Підтримує множинні варіанти
 * зберігання даних (наприклад, JSON, XML, PostgreSQL).
 */
public abstract class RepositoryFactory {

    /**
     * Константа для позначення типу фабрики, що працює з JSON.
     */
    public static final int JSON = 1;

    /**
     * Константа для позначення типу фабрики, що працює з XML.
     */
    public static final int XML = 2;

    /**
     * Константа для позначення типу фабрики, що працює з PostgreSQL.
     */
    public static final int POSTGRESQL = 3;

    /**
     * Повертає конкретну реалізацію фабрики залежно від вибраного типу.
     *
     * @param whichFactory ідентифікатор типу фабрики (JSON, XML, PostgreSQL).
     * @return реалізація фабрики {@link RepositoryFactory}.
     * @throws NotImplementedException якщо вибраний тип фабрики ще не реалізований.
     */
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

    /**
     * Повертає репозиторій для роботи з користувачами.
     *
     * @return реалізація {@link UserRepository}.
     */
    public abstract UserRepository getUserRepository();

    /**
     * Повертає репозиторій для роботи з криптовалютами.
     *
     * @return реалізація {@link CryptocurrencyRepository}.
     */
    public abstract CryptocurrencyRepository getCryptocurrencyRepository();

    /**
     * Повертає репозиторій для роботи з портфелями.
     *
     * @return реалізація {@link PortfolioRepository}.
     */
    public abstract PortfolioRepository getPortfolioRepository();

    /**
     * Повертає репозиторій для роботи з транзакціями.
     *
     * @return реалізація {@link TransactionRepository}.
     */
    public abstract TransactionRepository getTransactionRepository();

    /**
     * Повертає репозиторій для роботи з аутентифікаційними даними.
     *
     * @return реалізація {@link AuthDataRepository}.
     */
    public abstract AuthDataRepository getAuthDataRepository();

    /**
     * Зберігає всі зміни у репозиторіях.
     */
    public abstract void commit();
}
