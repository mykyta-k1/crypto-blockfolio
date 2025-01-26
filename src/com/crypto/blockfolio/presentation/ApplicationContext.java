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
    //private RepositoryFactory repositoryFactory;
    private ServiceFactory serviceFactory;
    // Репозиторії
    private UserRepository userRepository;
    private PortfolioRepository portfolioRepository;
    private CryptocurrencyRepository cryptocurrencyRepository;
    private TransactionRepository transactionRepository;

    private ApplicationContext() throws NotImplementedException {
        initializeDependencies();
    }

    public static ApplicationContext getInstance() throws NotImplementedException {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static SignUpService getSignUpService() {
        return signUpService;
    }

    // Методи для доступу до сервісів
    public static AuthService getAuthService() {
        return authService;
    }

    public static CryptocurrencyService getCryptocurrencyService() {
        return cryptocurrencyService;
    }

    public static RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }

    public static PortfolioService getPortfolioService() {
        return portfolioService;
    }

    public static CoinGeckoApiService getCoinGeckoApiService() {
        return coinGeckoApiService;
    }

    public static TransactionService getTransactionService() {
        return transactionService;
    }

    private void initializeDependencies() throws NotImplementedException {
        // Ініціалізація фабрик
        //repositoryFactory = RepositoryFactory.getRepositoryFactory(RepositoryFactory.JSON);
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

        System.out.println("Залежності успішно ініціалізовані.");
    }

    // Методи для доступу до репозиторіїв
    public UserRepository getUserRepository() {
        return userRepository;
    }

    public PortfolioRepository getPortfolioRepository() {
        return portfolioRepository;
    }

    public CryptocurrencyRepository getCryptocurrencyRepository() {
        return cryptocurrencyRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public void initializeCryptocurrencies() {
        CoinGeckoApiService coinGeckoApiService = ApplicationContext.getCoinGeckoApiService();
        coinGeckoApiService.getAllCryptocurrencies();
    }

}
