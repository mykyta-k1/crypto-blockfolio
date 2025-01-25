package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.AuthService;
import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.domain.contract.PortfolioService;
import com.crypto.blockfolio.domain.contract.SignUpService;
import com.crypto.blockfolio.domain.contract.TransactionService;
import com.crypto.blockfolio.domain.contract.UserService;
import com.crypto.blockfolio.persistence.repository.RepositoryFactory;

public final class ServiceFactory {

    private static volatile ServiceFactory INSTANCE;

    private final AuthService authService;
    private final UserService userService;
    private final SignUpService signUpService;
    private final CryptocurrencyService cryptocurrencyService;
    private final PortfolioService portfolioService;
    private final TransactionService transactionService;
    private final CoinGeckoApiService coinGeckoApiService;

    private final RepositoryFactory repositoryFactory;

    private ServiceFactory(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;

        var userRepository = repositoryFactory.getUserRepository();
        var cryptocurrencyRepository = repositoryFactory.getCryptocurrencyRepository();
        var portfolioRepository = repositoryFactory.getPortfolioRepository();
        var transactionRepository = repositoryFactory.getTransactionRepository();

        this.authService = new AuthServiceImpl(userRepository);
        this.userService = new UserServiceImpl(userRepository);
        this.signUpService = new SignUpServiceImpl(userService, authService);
        this.portfolioService = new PortfolioServiceImpl(portfolioRepository);
        this.transactionService = new TransactionServiceImpl(transactionRepository,
            portfolioRepository);
        this.coinGeckoApiService = new CoinGeckoApiServiceImpl();
        this.cryptocurrencyService = new CryptocurrencyServiceImpl(cryptocurrencyRepository,
            coinGeckoApiService);
    }

    /**
     * Використовувати, лише якщо впевнені, що існує об'єкт RepositoryFactory.
     *
     * @return екземпляр типу ServiceFactory
     */
    public static ServiceFactory getInstance() {
        if (INSTANCE == null || INSTANCE.repositoryFactory == null) {
            throw new IllegalStateException(
                "Спочатку потрібно викликати getInstance(RepositoryFactory repositoryFactory).");
        }
        return INSTANCE;
    }

    public static ServiceFactory getInstance(RepositoryFactory repositoryFactory) {
        if (INSTANCE == null) {
            synchronized (ServiceFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceFactory(repositoryFactory);
                }
            }
        }
        return INSTANCE;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public UserService getUserService() {
        return userService;
    }

    public SignUpService getSignUpService() {
        return signUpService;
    }

    public CryptocurrencyService getCryptocurrencyService() {
        return cryptocurrencyService;
    }

    public PortfolioService getPortfolioService() {
        return portfolioService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public CoinGeckoApiService getCoinGeckoApiService() {
        return coinGeckoApiService;
    }

    public RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }
}
