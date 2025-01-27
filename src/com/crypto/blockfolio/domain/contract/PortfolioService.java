package com.crypto.blockfolio.domain.contract;

import com.crypto.blockfolio.domain.Reportable;
import com.crypto.blockfolio.domain.Service;
import com.crypto.blockfolio.domain.dto.PortfolioAddDto;
import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.entity.Portfolio;
import java.util.List;
import java.util.UUID;

/**
 * Інтерфейс PortfolioService визначає сервіси для управління портфелями. Наслідує {@link Service}
 * для базових CRUD-операцій та {@link Reportable} для створення звітів.
 */
public interface PortfolioService extends Service<Portfolio, UUID>, Reportable<Portfolio> {

    /**
     * Додає новий портфель на основі переданих даних.
     *
     * @param portfolioAddDto об'єкт {@link PortfolioAddDto}, що містить дані нового портфеля.
     * @return об'єкт {@link Portfolio}, що представляє створений портфель.
     */
    Portfolio addPortfolio(PortfolioAddDto portfolioAddDto);

    /**
     * Отримує портфель за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор портфеля.
     * @return об'єкт {@link Portfolio}, що відповідає заданому ідентифікатору.
     */
    Portfolio getPortfolioById(UUID id);

    /**
     * Отримує список усіх портфелів.
     *
     * @return список об'єктів {@link Portfolio}.
     */
    List<Portfolio> getAllPortfolios();

    /**
     * Видаляє портфель за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор портфеля.
     */
    void deletePortfolio(UUID id);

    /**
     * Додає криптовалюту до портфеля.
     *
     * @param portfolioId    унікальний ідентифікатор портфеля.
     * @param cryptocurrency об'єкт {@link Cryptocurrency}, що додається до портфеля.
     */
    void addCryptocurrencyToPortfolio(UUID portfolioId, Cryptocurrency cryptocurrency);

    /**
     * Видаляє криптовалюту з портфеля.
     *
     * @param portfolioId    унікальний ідентифікатор портфеля.
     * @param cryptocurrency об'єкт {@link Cryptocurrency}, що видаляється з портфеля.
     */
    void removeCryptocurrencyFromPortfolio(UUID portfolioId, Cryptocurrency cryptocurrency);

    /**
     * Обчислює загальну вартість портфеля, враховуючи всі криптовалюти в ньому.
     *
     * @param portfolio об'єкт {@link Portfolio}, для якого виконується обчислення.
     */
    void calculateTotalValue(Portfolio portfolio);

    /**
     * Видаляє транзакцію з портфеля.
     *
     * @param portfolioId   унікальний ідентифікатор портфеля.
     * @param transactionId унікальний ідентифікатор транзакції.
     * @return {@code true}, якщо транзакцію успішно видалено, інакше {@code false}.
     */
    boolean removeTransactionFromPortfolio(UUID portfolioId, UUID transactionId);
}

