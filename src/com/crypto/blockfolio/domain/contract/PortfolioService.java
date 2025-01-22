package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.Reportable;
import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.domain.dto.PortfolioAddDto;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import java.util.List;
import java.util.UUID;

public interface PortfolioService extends Service<Portfolio>, Reportable<Portfolio> {

    Portfolio addPortfolio(PortfolioAddDto portfolioAddDto);

    Portfolio getPortfolioById(UUID id);

    List<Portfolio> getAllPortfolios();

    void deletePortfolio(UUID id);

    void addCryptocurrencyToPortfolio(UUID portfolioId, Cryptocurrency cryptocurrency);

    void removeCryptocurrencyFromPortfolio(UUID portfolioId, Cryptocurrency cryptocurrency);

    void calculateTotalValue(UUID portfolioId);
}
