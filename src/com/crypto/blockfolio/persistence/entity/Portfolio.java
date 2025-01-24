package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
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
    private final Map<Cryptocurrency, BigDecimal> balances; // Баланси криптовалют
    private final Set<UUID> transactionsList; // Список транзакцій (за ID)
    private String name;
    private BigDecimal totalValue;

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

        balances.putIfAbsent(cryptocurrency, BigDecimal.ZERO); // Додаємо, якщо відсутня
    }

    public void removeCryptocurrency(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || !balances.containsKey(cryptocurrency)) {
            errors.add("Криптовалюта не знайдена у портфелі.");
        } else {
            balances.remove(cryptocurrency);
        }
    }

    public void addTransaction(UUID transactionId, Cryptocurrency cryptocurrency, BigDecimal amount,
        TransactionType transactionType) {
        if (transactionId == null) {
            errors.add("ID транзакції не може бути null.");
            return;
        }

        if (cryptocurrency == null || !cryptocurrency.isValid()) {
            errors.add("Криптовалюта не є валідною.");
            return;
        }

        // Додаємо криптовалюту до портфеля, якщо її ще немає
        balances.putIfAbsent(cryptocurrency, BigDecimal.ZERO);

        // Додаємо транзакцію
        if (transactionsList.add(transactionId)) {
            // Оновлюємо баланс
            BigDecimal currentBalance = balances.get(cryptocurrency);
            BigDecimal updatedBalance = currentBalance.add(
                transactionType == TransactionType.BUY ? amount : amount.negate()
            );

            balances.put(cryptocurrency, updatedBalance);
            calculateTotalValue(); // Перераховуємо загальну вартість портфеля
        } else {
            errors.add("Транзакція вже існує в портфелі.");
        }
    }

    /**
     * Оновлює загальну вартість портфеля на основі балансу криптовалют.
     */
    public void calculateTotalValue() {
        this.totalValue = balances.entrySet().stream()
            .map(entry -> BigDecimal.valueOf(entry.getKey().getCurrentPrice())
                .multiply(entry.getValue()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean removeTransaction(UUID transactionId,
        TransactionRepository transactionRepository) {
        if (transactionId == null) {
            errors.add("ID транзакції не може бути null.");
            return false;
        }

        // Видаляємо транзакцію зі списку транзакцій портфоліо
        boolean removed = transactionsList.remove(transactionId);
        if (!removed) {
            errors.add("Транзакція з ID " + transactionId + " не знайдена в портфоліо.");
            return false;
        }

        // Якщо транзакцію видалено, оновлюємо баланси та загальну вартість
        transactionRepository.findById(transactionId).ifPresent(transaction -> {
            Cryptocurrency cryptocurrency = transaction.getCryptocurrency();

            // Перераховуємо баланс відповідної криптовалюти
            BigDecimal updatedBalance = calculateBalanceForCryptocurrency(cryptocurrency,
                transactionRepository);

            // Якщо баланс криптовалюти дорівнює 0, видаляємо криптовалюту з балансу
            if (updatedBalance.compareTo(BigDecimal.ZERO) == 0) {
                balances.remove(cryptocurrency);
            } else {
                balances.put(cryptocurrency, updatedBalance);
            }

            // Оновлюємо загальну вартість портфоліо
            calculateTotalValue();
        });

        return true;
    }

    public BigDecimal calculateBalanceForCryptocurrency(Cryptocurrency cryptocurrency,
        TransactionRepository transactionRepository) {
        if (cryptocurrency == null) {
            errors.add("Криптовалюта не може бути null.");
            return BigDecimal.ZERO;
        }

        // Отримуємо всі релевантні транзакції для цієї криптовалюти
        Set<Transaction> relevantTransactions = transactionRepository.findAll(transaction ->
            transaction.getCryptocurrency().equals(cryptocurrency) &&
                transactionsList.contains(transaction.getId())
        );

        // Обчислюємо баланс, враховуючи тип транзакції
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

    public Map<Cryptocurrency, BigDecimal> getBalances() {
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
