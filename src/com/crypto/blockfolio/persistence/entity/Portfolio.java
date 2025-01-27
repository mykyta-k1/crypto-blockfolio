package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import com.crypto.blockfolio.presentation.ApplicationContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Клас, що представляє портфель користувача. Портфель містить інформацію про баланси криптовалют,
 * транзакції та загальну вартість. Також надає методи для управління криптовалютами, транзакціями
 * та обчислення прибутків і збитків (PNL).
 */
public class Portfolio extends Entity implements Comparable<Portfolio> {

    /**
     * Ідентифікатор власника портфеля.
     */
    private final UUID ownerId;
    /**
     * Час створення портфеля.
     */
    private final LocalDateTime createdAt;
    /**
     * Баланси криптовалют у портфелі. Ключ – символ криптовалюти, значення – кількість.
     */
    private final Map<String, BigDecimal> balances;
    /**
     * Список транзакцій, пов'язаних із портфелем (зберігаються ID транзакцій).
     */
    private final Set<UUID> transactionsList;
    /**
     * Загальна вартість портфеля.
     */
    private BigDecimal totalValue;
    /**
     * Назва портфеля.
     */
    private String name;

    /**
     * Конструктор для створення нового портфеля.
     *
     * @param id      унікальний ідентифікатор портфеля.
     * @param ownerId ідентифікатор власника портфеля.
     * @param name    назва портфеля.
     * @throws EntityArgumentException якщо дані некоректні.
     */
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
     * Додає криптовалюту до портфеля, якщо вона ще не існує.
     *
     * @param cryptocurrency об'єкт криптовалюти, яку додають.
     */
    public void addCryptocurrency(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || !cryptocurrency.isValid()) {
            errors.add("Криптовалюта не є валідною.");
            return;
        }

        balances.putIfAbsent(cryptocurrency.getSymbol(), BigDecimal.ZERO);
    }

    /**
     * Видаляє криптовалюту з портфеля.
     *
     * @param cryptocurrency об'єкт криптовалюти, яку потрібно видалити.
     */
    public void removeCryptocurrency(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || !balances.containsKey(cryptocurrency)) {
            errors.add("Криптовалюта не знайдена у портфелі.");
        } else {
            balances.remove(cryptocurrency);
        }
    }

    /**
     * Додає транзакцію до портфеля, оновлюючи баланс криптовалюти та загальну вартість.
     *
     * @param transactionId            ідентифікатор транзакції.
     * @param cryptocurrency           об'єкт криптовалюти.
     * @param amount                   кількість криптовалюти в транзакції.
     * @param transactionType          тип транзакції (купівля/продаж).
     * @param cryptocurrencyRepository репозиторій криптовалют для отримання поточних даних.
     */
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

        balances.putIfAbsent(cryptocurrency.getSymbol(), BigDecimal.ZERO);

        BigDecimal currentBalance = balances.get(cryptocurrency.getSymbol());
        BigDecimal updatedBalance = currentBalance.add(
            transactionType == TransactionType.BUY ? amount : amount.negate()
        );

        balances.put(cryptocurrency.getSymbol(), updatedBalance);
        calculateTotalValue(cryptocurrencyRepository);
    }

    /**
     * Перераховує загальну вартість портфеля, базуючись на поточних цінах криптовалют.
     *
     * @param cryptocurrencyRepository репозиторій криптовалют для отримання актуальних цін.
     */
    public void calculateTotalValue(CryptocurrencyRepository cryptocurrencyRepository) {
        this.totalValue = balances.entrySet().stream()
            .map(entry -> {
                String symbol = entry.getKey();
                BigDecimal balance = entry.getValue();

                return cryptocurrencyRepository.findBySymbol(symbol)
                    .map(crypto -> BigDecimal.valueOf(crypto.getCurrentPrice()).multiply(balance))
                    .orElse(
                        BigDecimal.ZERO);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Видаляє транзакцію з портфеля та оновлює баланси та загальну вартість.
     *
     * @param transactionId            ідентифікатор транзакції, яку потрібно видалити.
     * @param transactionRepository    репозиторій транзакцій для доступу до деталей транзакцій.
     * @param cryptocurrencyRepository репозиторій криптовалют для оновлення загальної вартості.
     * @return {@code true}, якщо транзакцію успішно видалено, інакше {@code false}.
     */
    public boolean removeTransaction(UUID transactionId,
        TransactionRepository transactionRepository,
        CryptocurrencyRepository cryptocurrencyRepository) {
        if (transactionId == null) {
            errors.add("ID транзакції не може бути null.");
            return false;
        }

        boolean removed = transactionsList.remove(transactionId);
        if (!removed) {
            errors.add("Транзакція з ID " + transactionId + " не знайдена в портфелі.");
            return false;
        }

        transactionRepository.findById(transactionId).ifPresent(transaction -> {
            String symbol = transaction.getCryptocurrency().getSymbol();

            BigDecimal updatedBalance = calculateBalanceForCryptocurrency(symbol,
                transactionRepository);

            balances.put(symbol, updatedBalance);

            calculateTotalValue(cryptocurrencyRepository);
        });

        return true;
    }

    /**
     * Обчислює баланс для заданої криптовалюти на основі транзакцій у портфелі.
     *
     * @param symbol                символ криптовалюти, для якої обчислюється баланс.
     * @param transactionRepository репозиторій транзакцій для отримання даних про транзакції.
     * @return загальний баланс криптовалюти. Повертає {@code BigDecimal.ZERO}, якщо символ порожній
     * або не існує транзакцій для даного символу.
     */
    public BigDecimal calculateBalanceForCryptocurrency(String symbol,
        TransactionRepository transactionRepository) {
        if (symbol == null || symbol.trim().isEmpty()) {
            errors.add("Символ криптовалюти не може бути null або порожнім.");
            return BigDecimal.ZERO;
        }

        Set<Transaction> relevantTransactions = transactionRepository.findAll(transaction ->
            transaction.getCryptocurrency().getSymbol().equalsIgnoreCase(symbol) &&
                transactionsList.contains(transaction.getId())
        );

        return relevantTransactions.stream()
            .map(transaction -> {
                BigDecimal amount = transaction.getAmount();
                return transaction.getTransactionType() == TransactionType.BUY ? amount
                    : amount.negate();
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Обчислює PNL (прибутки та збитки) для заданої криптовалюти.
     *
     * @param symbol символ криптовалюти.
     * @return загальний PNL для криптовалюти.
     */
    public BigDecimal calculatePnlForCryptocurrency(String symbol) {
        try {
            TransactionRepository transactionRepository = ApplicationContext.getInstance()
                .getTransactionRepository();

            BigDecimal totalPnl = BigDecimal.ZERO;

            for (UUID transactionId : transactionsList) {
                Optional<Transaction> transactionOpt = transactionRepository.findById(
                    transactionId);

                if (transactionOpt.isPresent()) {
                    Transaction transaction = transactionOpt.get();
                    if (transaction.getCryptocurrency().getSymbol().equalsIgnoreCase(symbol)) {
                        BigDecimal pnl = transaction.calculatePnl();
                        totalPnl = totalPnl.add(pnl != null ? pnl : BigDecimal.ZERO);
                    }
                }
            }

            return totalPnl;
        } catch (Exception e) {
            System.err.println(
                "Помилка підрахунку PNL для криптовалюти " + symbol + ": " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Обчислює сумарний PNL (прибутки та збитки) для всіх криптовалют у портфелі.
     *
     * @return сумарний PNL для всіх криптовалют.
     */
    public BigDecimal calculateTotalPnl() {
        try {
            TransactionRepository transactionRepository = ApplicationContext.getInstance()
                .getTransactionRepository();
            BigDecimal totalPnl = BigDecimal.ZERO;
            for (UUID transactionId : transactionsList) {
                Optional<Transaction> transactionOpt = transactionRepository.findById(
                    transactionId);

                if (transactionOpt.isPresent()) {
                    Transaction transaction = transactionOpt.get();
                    totalPnl = totalPnl.add(transaction.calculatePnl());
                }
            }

            return totalPnl;
        } catch (Exception e) {
            System.err.println("Помилка підрахунку сумарного PNL: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Порівнює портфелі за назвою.
     *
     * @param o інший портфель.
     * @return результат порівняння.
     */
    @Override
    public int compareTo(Portfolio o) {
        return this.name.compareTo(o.name);
    }

    /**
     * Повертає час створення портфеля.
     *
     * @return час створення портфеля.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Повертає список транзакцій у портфелі.
     *
     * @return набір ідентифікаторів транзакцій.
     */
    public Set<UUID> getTransactionsList() {
        return transactionsList;
    }

    /**
     * Повертає баланси криптовалют у портфелі.
     *
     * @return мапа, де ключ – символ криптовалюти, значення – її кількість.
     */
    public Map<String, BigDecimal> getBalances() {
        return balances;
    }

    /**
     * Повертає ідентифікатор власника портфеля.
     *
     * @return ідентифікатор власника.
     */
    public UUID getOwnerId() {
        return ownerId;
    }

    /**
     * Повертає назву портфеля.
     *
     * @return назва портфеля.
     */
    public String getName() {
        return name;
    }

    /**
     * Встановлює нову назву портфеля.
     *
     * @param name нова назва портфеля.
     */
    public void setName(String name) {
        final String templateName = "назви";
        name = name != null ? name.trim() : null;

        ValidationUtils.validateRequired(name, templateName, errors);
        ValidationUtils.validateLength(name, 1, 64, templateName, errors);
        ValidationUtils.validatePattern(name, "^[а-яА-ЯёЁa-zA-Z0-9_\\s]+$", templateName, errors);
        this.name = name;
    }

    /**
     * Повертає загальну вартість портфеля.
     *
     * @return загальна вартість.
     */
    public BigDecimal getTotalValue() {
        return totalValue;
    }

    /**
     * Встановлює загальну вартість портфеля.
     *
     * @param totalValue нова загальна вартість портфеля.
     */
    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    /**
     * Повертає строкове представлення портфеля.
     *
     * @return строкове представлення.
     */
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
