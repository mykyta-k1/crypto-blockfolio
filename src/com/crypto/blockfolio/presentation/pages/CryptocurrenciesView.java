package com.crypto.blockfolio.presentation.pages;

import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.presentation.ApplicationContext;
import com.crypto.blockfolio.presentation.ViewService;
import java.util.Scanner;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class CryptocurrenciesView implements ViewService {

    private static final int PAGE_SIZE = 20; // Number of cryptocurrencies per page
    private final CryptocurrencyService cryptocurrencyService;
    private final Scanner scanner;

    public CryptocurrenciesView() throws NotImplementedException {
        this.cryptocurrencyService = ApplicationContext.getCryptocurrencyService();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void display() {

    }
}
