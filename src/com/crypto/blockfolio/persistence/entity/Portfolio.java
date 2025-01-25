package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Portfolio extends Entity implements Comparable<Portfolio> {

    private final UUID ownerId;
    private final LocalDateTime createdAt;
    private final Map<String, BigDecimal> balances;
    private final Set<UUID> transactionsList; // Список транзакцій (за ID)
    private BigDecimal totalValue;
    private String name;

    public Portfolio(UUID id, UUID ownerId, String name) {
        super(id);
        this.ownerId = ownerId;
        this.name = name;
        this.totalValue = BigDecimal.ZERO;
        this.balances = new HashMap<>();
        this.transactionsList = new LinkedHashSet<>();
        this.createdAt = LocalDateTime.now();

        if (!this.isValid()) {
            throw new EntityArgumentException(errors);
        }
    }

    /**
     * Додає криптовалюту до портфеля, якщо її ще немає в balances.
     */
    public void addCryptocurrency(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || !cryptocurrency.isValid()) {
            errors.add("Криптовалюта не є валідною.");
            return;
        }

        balances.putIfAbsent(cryptocurrency.getSymbol(), BigDecimal.ZERO); // Додаємо, якщо відсутня
    }

    public void removeCryptocurrency(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || !balances.containsKey(cryptocurrency)) {
            errors.add("Криптовалюта не знайдена у портфелі.");
        } else {
            balances.remove(cryptocurrency);
        }
    }

    public void addTransaction(UUID transactionId, Cryptocurrency cryptocurrency, BigDecimal amount,
        TransactionType transactionType, CryptocurrencyRepository cryptocurrencyRepository) {
        if (transactionId == null) {
            errors.add("ID транзакції не може бути null.");
            return;
        }

        if (cryptocurrency == null || !cryptocurrency.isValid()) {
            errors.add("Криптовалюта не є валідною.");
            return;
        }

        if (!transactionsList.add(transactionId)) {
            errors.add("Транзакція вже існує в портфелі.");
            return;
        }

        // Додаємо криптовалюту до портфеля, якщо її ще немає
        balances.putIfAbsent(cryptocurrency.getSymbol(), BigDecimal.ZERO);

        BigDecimal currentBalance = balances.get(cryptocurrency.getSymbol());
        BigDecimal updatedBalance = currentBalance.add(
            transactionType == TransactionType.BUY ? amount : amount.negate()
        );

        balances.put(cryptocurrency.getSymbol(), updatedBalance);
        calculateTotalValue(cryptocurrencyRepository); // Перераховуємо загальну вартість портфеля
    }

    public void calculateTotalValue(CryptocurrencyRepository cryptocurrencyRepository) {
        this.totalValue = balances.entrySet().stream()
            .map(entry -> {
                String symbol = entry.getKey();
                BigDecimal balance = entry.getValue();

                // Отримуємо криптовалюту з репозиторію
                return cryptocurrencyRepository.findBySymbol(symbol)
                    .map(crypto -> BigDecimal.valueOf(crypto.getCurrentPrice()).multiply(balance))
                    .orElse(
                        BigDecimal.ZERO); // Якщо криптовалюта не знайдена, вважаємо її вартість 0
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add); // Підсумовуємо всі вартості
    }

    public boolean removeTransaction(UUID transactionId,
        TransactionRepository transactionRepository,
        CryptocurrencyRepository cryptocurrencyRepository) {
        if (transactionId == null) {
            errors.add("ID транзакції не може бути null.");
            return false;
        }

        // Видаляємо транзакцію зі списку транзакцій портфоліо
        boolean removed = transactionsList.remove(transactionId);
        if (!removed) {
            errors.add("Транзакція з ID " + transactionId + " не знайдена в портфелі.");
            return false;
        }

        // Якщо транзакцію видалено, оновлюємо баланси та загальну вартість
        transactionRepository.findById(transactionId).ifPresent(transaction -> {
            String symbol = transaction.getCryptocurrency().getSymbol();

            // Перераховуємо баланс відповідної криптовалюти
            BigDecimal updatedBalance = calculateBalanceForCryptocurrency(symbol,
                transactionRepository);

            // Зберігаємо криптовалюту із оновленим балансом
            balances.put(symbol, updatedBalance);

            // Оновлюємо загальну вартість портфоліо
            calculateTotalValue(cryptocurrencyRepository);
        });

        return true;
    }


    public BigDecimal calculateBalanceForCryptocurrency(String symbol,
        TransactionRepository transactionRepository) {
        if (symbol == null || symbol.trim().isEmpty()) {
            errors.add("Символ криптовалюти не може бути null або порожнім.");
            return BigDecimal.ZERO;
        }

        // Отримуємо всі релевантні транзакції для цього символу
        Set<Transaction> relevantTransactions = transactionRepository.findAll(transaction ->
            transaction.getCryptocurrency().getSymbol().equalsIgnoreCase(symbol) &&
                transactionsList.contains(transaction.getId())
        );

        // Обчислюємо баланс криптовалюти, враховуючи тип транзакції (купівля/продаж)
        return relevantTransactions.stream()
            .map(transaction -> {
                BigDecimal amount = transaction.getAmount();
                return transaction.getTransactionType() == TransactionType.BUY ? amount
                    : amount.negate();
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add); // Підсумовуємо всі значення
    }

    @Override
    public int compareTo(Portfolio o) {
        return this.name.compareTo(o.name);
    }

    // Гетери та сетери
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<UUID> getTransactionsList() {
        return transactionsList;
    }

    public Map<String, BigDecimal> getBalances() {
        return balances;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        final String templateName = "назви";
        name = name != null ? name.trim() : null;

        ValidationUtils.validateRequired(name, templateName, errors);
        ValidationUtils.validateLength(name, 1, 64, templateName, errors);
        this.name = name;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    @Override
    public String toString() {
        return "Portfolio{" +
            "name='" + name + '\'' +
            ", totalValue=" + totalValue +
            ", owner=" + ownerId +
            ", createdAt=" + createdAt +
            ", balances=" + balances +
            ", transactionsList=" + transactionsList +
            '}';
    }
}
