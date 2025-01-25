package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import java.util.List;
import java.util.function.Predicate;

class CryptocurrencyServiceImpl extends GenericService<Cryptocurrency, String> implements
    CryptocurrencyService {

    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final CoinGeckoApiService apiService;

    public CryptocurrencyServiceImpl(CryptocurrencyRepository cryptocurrencyRepository,
        CoinGeckoApiService apiService) {
        super(cryptocurrencyRepository);
        this.cryptocurrencyRepository = cryptocurrencyRepository;
        this.apiService = apiService;
    }

    @Override
    public Cryptocurrency getCryptocurrencyInfo(String symbol) {
        return null;
    }

    @Override
    public List<Cryptocurrency> getAllCryptocurrencies() {
        try {
            // Fetch cryptocurrencies from the API
            List<Cryptocurrency> cryptocurrencies = apiService.getAllCryptocurrencies();

            // Save or update each cryptocurrency in the repository
            cryptocurrencies.forEach(crypto -> {
                try {
                    cryptocurrencyRepository.updateCryptocurrency(crypto);
                } catch (IllegalArgumentException e) {
                    System.err.printf("Помилка оновлення криптовалюти %s: %s%n", crypto.getSymbol(),
                        e.getMessage());
                }
            });

            // Return the updated list of cryptocurrencies
            return List.copyOf(cryptocurrencyRepository.findAll());
        } catch (Exception e) {
            System.err.printf("Помилка отримання криптовалют з API: %s%n", e.getMessage());

            // If API fails, fall back to data from the repository
            return List.copyOf(cryptocurrencyRepository.findAll());
        }
    }


    @Override
    public void generateReport(Predicate<Cryptocurrency> predicate) {
        // реалізації не буде...
    }
}
