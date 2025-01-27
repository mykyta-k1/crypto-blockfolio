package com.crypto.blockfolio.presentation;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.domain.contract.PortfolioService;
import com.crypto.blockfolio.domain.contract.SignUpService;
import com.crypto.blockfolio.domain.contract.TransactionService;
import com.crypto.blockfolio.domain.contract.UserService;
import com.crypto.blockfolio.domain.impl.ServiceFactory;
import com.crypto.blockfolio.persistence.repository.RepositoryFactory;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.crypto.blockfolio.persistence.repository.impl.json.JsonRepositoryFactory;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * Клас контексту застосунку, що відповідає за ініціалізацію та надання доступу до сервісів і
 * репозиторіїв. Реалізує патерн Singleton для централізованого управління залежностями.
 */
public class ApplicationContext {

    private static final RepositoryFactory repositoryFactory = JsonRepositoryFactory.getInstance();
    private static ApplicationContext instance;

    // Сервіси
    private static AuthService authService;
    private static UserService userService;
    private static SignUpService signUpService;
    private static CryptocurrencyService cryptocurrencyService;
    private static PortfolioService portfolioService;
    private static CoinGeckoApiService coinGeckoApiService;
    private static TransactionService transactionService;

    // Фабрики
    private ServiceFactory serviceFactory;

    // Репозиторії
    private UserRepository userRepository;
    private PortfolioRepository portfolioRepository;
    private CryptocurrencyRepository cryptocurrencyRepository;
    private TransactionRepository transactionRepository;

    /**
     * Приватний конструктор для забезпечення патерну Singleton. Ініціалізує залежності.
     *
     * @throws NotImplementedException якщо фабрики не підтримують реалізацію.
     */
    private ApplicationContext() throws NotImplementedException {
        initializeDependencies();
    }

    /**
     * Повертає єдиний екземпляр {@link ApplicationContext}.
     *
     * @return екземпляр контексту застосунку.
     * @throws NotImplementedException якщо фабрики не підтримують реалізацію.
     */
    public static ApplicationContext getInstance() throws NotImplementedException {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    /**
     * Повертає сервіс для роботи з користувачами.
     *
     * @return екземпляр {@link UserService}.
     */
    public static UserService getUserService() {
        return userService;
    }

    /**
     * Повертає сервіс реєстрації.
     *
     * @return екземпляр {@link SignUpService}.
     */
    public static SignUpService getSignUpService() {
        return signUpService;
    }

    /**
     * Повертає сервіс авторизації.
     *
     * @return екземпляр {@link AuthService}.
     */
    public static AuthService getAuthService() {
        return authService;
    }

    /**
     * Повертає сервіс для роботи з криптовалютами.
     *
     * @return екземпляр {@link CryptocurrencyService}.
     */
    public static CryptocurrencyService getCryptocurrencyService() {
        return cryptocurrencyService;
    }

    /**
     * Повертає сервіс для роботи з портфелями.
     *
     * @return екземпляр {@link PortfolioService}.
     */
    public static PortfolioService getPortfolioService() {
        return portfolioService;
    }

    /**
     * Повертає сервіс для інтеграції з API CoinGecko.
     *
     * @return екземпляр {@link CoinGeckoApiService}.
     */
    public static CoinGeckoApiService getCoinGeckoApiService() {
        return coinGeckoApiService;
    }

    /**
     * Повертає сервіс для роботи з транзакціями.
     *
     * @return екземпляр {@link TransactionService}.
     */
    public static TransactionService getTransactionService() {
        return transactionService;
    }

    /**
     * Повертає фабрику репозиторіїв.
     *
     * @return екземпляр {@link RepositoryFactory}.
     */
    public static RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }

    /**
     * Ініціалізує всі залежності, включаючи фабрики, репозиторії та сервіси.
     *
     * @throws NotImplementedException якщо фабрики не підтримують реалізацію.
     */
    private void initializeDependencies() throws NotImplementedException {
        // Ініціалізація фабрик
        serviceFactory = ServiceFactory.getInstance(repositoryFactory);

        // Ініціалізація репозиторіїв
        userRepository = repositoryFactory.getUserRepository();
        portfolioRepository = repositoryFactory.getPortfolioRepository();
        cryptocurrencyRepository = repositoryFactory.getCryptocurrencyRepository();
        transactionRepository = repositoryFactory.getTransactionRepository();

        // Ініціалізація сервісів
        authService = serviceFactory.getAuthService();
        userService = serviceFactory.getUserService();
        portfolioService = serviceFactory.getPortfolioService();
        cryptocurrencyService = serviceFactory.getCryptocurrencyService();
        signUpService = serviceFactory.getSignUpService();
        coinGeckoApiService = serviceFactory.getCoinGeckoApiService();
        transactionService = serviceFactory.getTransactionService();
    }

    /**
     * Повертає репозиторій для роботи з користувачами.
     *
     * @return екземпляр {@link UserRepository}.
     */
    public UserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * Повертає репозиторій для роботи з портфелями.
     *
     * @return екземпляр {@link PortfolioRepository}.
     */
    public PortfolioRepository getPortfolioRepository() {
        return portfolioRepository;
    }

    /**
     * Повертає репозиторій для роботи з криптовалютами.
     *
     * @return екземпляр {@link CryptocurrencyRepository}.
     */
    public CryptocurrencyRepository getCryptocurrencyRepository() {
        return cryptocurrencyRepository;
    }

    /**
     * Повертає репозиторій для роботи з транзакціями.
     *
     * @return екземпляр {@link TransactionRepository}.
     */
    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    /**
     * Ініціалізує криптовалюти за допомогою API CoinGecko.
     */
    public void initializeCryptocurrencies() {
        CoinGeckoApiService coinGeckoApiService = ApplicationContext.getCoinGeckoApiService();
        coinGeckoApiService.getAllCryptocurrencies();
    }

}
