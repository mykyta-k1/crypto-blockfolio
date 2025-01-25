package com.crypto.blockfolio.presentation;

import com.crypto.blockfolio.domain.contract.AuthService;
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
import com.crypto.blockfolio.presentation.pages.DashBoardView;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class Main {

    private static RepositoryFactory repositoryFactory;
    private static ServiceFactory serviceFactory;

    public static void main(String[] args) throws NotImplementedException {
        initializeDependencies();

        // Запускаємо головну сторінку
        DashBoardView dashBoardView = new DashBoardView();
        dashBoardView.display();
    }

    private static void initializeDependencies() throws NotImplementedException {
        // Ініціалізація фабрик репозиторіїв та сервісів
        repositoryFactory = RepositoryFactory.getRepositoryFactory(RepositoryFactory.JSON);
        serviceFactory = ServiceFactory.getInstance(repositoryFactory);

        System.out.println("Залежності успішно ініціалізовані.");
    }

    // Глобальний доступ до репозиторіїв
    public static UserRepository getUserRepository() {
        return repositoryFactory.getUserRepository();
    }

    public static CryptocurrencyRepository getCryptocurrencyRepository() {
        return repositoryFactory.getCryptocurrencyRepository();
    }

    public static PortfolioRepository getPortfolioRepository() {
        return repositoryFactory.getPortfolioRepository();
    }

    public static TransactionRepository getTransactionRepository() {
        return repositoryFactory.getTransactionRepository();
    }

    // Глобальний доступ до сервісів
    public static UserService getUserService() {
        return serviceFactory.getUserService();
    }

    public static SignUpService getSignUpService() {
        return serviceFactory.getSignUpService();
    }

    public static CryptocurrencyService getCryptocurrencyService() {
        return serviceFactory.getCryptocurrencyService();
    }

    public static PortfolioService getPortfolioService() {
        return serviceFactory.getPortfolioService();
    }

    public static TransactionService getTransactionService() {
        return serviceFactory.getTransactionService();
    }

    public static AuthService getAuthService() {
        return serviceFactory.getAuthService();
    }
}
