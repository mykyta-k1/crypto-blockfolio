package com.crypto.blockfolio.domain.impl;

import com.crypto.blockfolio.domain.contract.CoinGeckoApiService;
import com.crypto.blockfolio.domain.contract.CryptocurrencyService;
import com.crypto.blockfolio.domain.dto.CryptocurrencyAddDto;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CryptocurrencyServiceImpl implements CryptocurrencyService {

    private final CryptocurrencyRepository cryptocurrencyRepository;
    private final CoinGeckoApiService apiService;

    public CryptocurrencyServiceImpl(CryptocurrencyRepository cryptocurrencyRepository,
        CoinGeckoApiService apiService) {
        this.cryptocurrencyRepository = cryptocurrencyRepository;
        this.apiService = apiService;
    }

    @Override
    public Cryptocurrency addCryptocurrency(CryptocurrencyAddDto cryptocurrencyAddDto) {
        Cryptocurrency cryptocurrency = new Cryptocurrency(
            cryptocurrencyAddDto.getId(),
            cryptocurrencyAddDto.getName(),
            cryptocurrencyAddDto.getCurrentPrice()
        );
        return cryptocurrencyRepository.add(cryptocurrency);
    }

    @Override
    public void syncCryptocurrencyPrice(String name) {
        Cryptocurrency updatedCryptocurrency = apiService.getCryptocurrencyInfo(name);
        Cryptocurrency existingCryptocurrency = cryptocurrencyRepository.findByName(name)
            .orElseThrow(
                () -> new RuntimeException("Криптовалюта з назвою %s не знайдена".formatted(name)));

        existingCryptocurrency.setCurrentPrice(updatedCryptocurrency.getCurrentPrice());
        cryptocurrencyRepository.update(existingCryptocurrency);

        System.out.printf("Ціна для монети %s оновлена до %.2f%n", name,
            updatedCryptocurrency.getCurrentPrice());
    }

    // Використання методів із базового інтерфейсу Service

    @Override
    public Cryptocurrency get(UUID id) {
        return cryptocurrencyRepository.findById(id)
            .orElseThrow(
                () -> new RuntimeException("Криптовалюта з ID %s не знайдена".formatted(id)));
    }

    @Override
    public Set<Cryptocurrency> getAll() {
        return cryptocurrencyRepository.findAll();
    }

    @Override
    public Set<Cryptocurrency> getAll(Predicate<Cryptocurrency> filter) {
        return cryptocurrencyRepository.findAll().stream()
            .filter(filter)
            .collect(Collectors.toSet());
    }

    @Override
    public Cryptocurrency add(Cryptocurrency entity) {
        return cryptocurrencyRepository.add(entity);
    }

    @Override
    public boolean remove(Cryptocurrency entity) {
        return cryptocurrencyRepository.remove(entity);
    }
}
