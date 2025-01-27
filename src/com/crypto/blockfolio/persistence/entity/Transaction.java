package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

/**
 * Клас, що представляє транзакцію. Транзакція включає інформацію про криптовалюту, тип операції,
 * кількість, витрати, прибуток, комісії та опис. Також підтримує обчислення PNL (прибуток/збиток).
 */
public class Transaction extends Entity implements Comparable<Transaction> {

    /**
     * Ідентифікатор портфеля, до якого належить транзакція.
     */
    private UUID portfolioId;
    /**
     * Дата та час створення транзакції.
     */
    private LocalDateTime createdAt;
    /**
     * Об'єкт криптовалюти, яка бере участь у транзакції.
     */
    private Cryptocurrency cryptocurrency;
    /**
     * Тип транзакції (купівля, продаж, переказ тощо).
     */
    private TransactionType transactionType;
    /**
     * Кількість криптовалюти у транзакції.
     */
    private BigDecimal amount;
    /**
     * Витрати, пов'язані з транзакцією.
     */
    private BigDecimal costs;
    /**
     * Прибуток, отриманий від транзакції (тільки для продажу).
     */
    private BigDecimal profit;
    /**
     * Комісії, сплачені за транзакцію.
     */
    private BigDecimal fees;
    /**
     * Опис транзакції.
     */
    private String description;

    /**
     * Конструктор для створення нового екземпляра {@link Transaction}.
     *
     * @param id              унікальний ідентифікатор транзакції.
     * @param portfolioId     ідентифікатор портфеля, до якого належить транзакція.
     * @param cryptocurrency  об'єкт криптовалюти, яка бере участь у транзакції.
     * @param transactionType тип транзакції.
     * @param amount          кількість криптовалюти.
     * @param costs           витрати, пов'язані з транзакцією.
     * @param profit          прибуток (для продажу).
     * @param fees            комісії за транзакцію.
     * @param description     опис транзакції.
     * @param createdAt       дата створення транзакції.
     * @throws EntityArgumentException якщо вхідні дані некоректні.
     */
    public Transaction(UUID id, UUID portfolioId, Cryptocurrency cryptocurrency,
        TransactionType transactionType,
        BigDecimal amount, BigDecimal costs, BigDecimal profit, BigDecimal fees, String description,
        LocalDateTime createdAt) {
        super(id);
        setCryptocurrency(cryptocurrency);
        setTransactionType(transactionType);
        setAmount(amount);
        setCosts(costs);
        this.portfolioId = portfolioId;
        this.profit = profit;
        this.fees = fees;
        this.description = description;
        this.createdAt = validateCreatedAt(createdAt);

        if (!this.isValid()) {
            System.err.println("Помилки транзакції: " + errors);
            throw new EntityArgumentException(errors);
        }
    }

    /**
     * Порівнює транзакції за часом їх створення.
     *
     * @param o інша транзакція.
     * @return результат порівняння.
     */
    @Override
    public int compareTo(Transaction o) {
        return this.createdAt.compareTo(o.createdAt);
    }

    /**
     * Повертає криптовалюту, яка бере участь у транзакції.
     *
     * @return об'єкт криптовалюти.
     */
    public Cryptocurrency getCryptocurrency() {
        return cryptocurrency;
    }

    /**
     * Встановлює криптовалюту для транзакції.
     *
     * @param cryptocurrency об'єкт криптовалюти.
     */
    public void setCryptocurrency(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || !cryptocurrency.isValid()) {
            errors.add("Криптомонета не є валідною.");
        } else {
            this.cryptocurrency = cryptocurrency;
        }
    }

    /**
     * Обчислює прибуток або збиток (PNL) для даної транзакції.
     *
     * @return значення PNL, округлене до 2-х знаків після коми.
     */
    public BigDecimal calculatePnl() {
        if (cryptocurrency == null || cryptocurrency.getCurrentPrice() <= 0 || amount == null
            || fees == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal currentValue = BigDecimal.valueOf(cryptocurrency.getCurrentPrice())
            .multiply(amount);

        switch (transactionType) {
            case BUY:
                return currentValue.subtract(costs).subtract(fees)
                    .setScale(2, RoundingMode.HALF_UP);

            case SELL:
                if (profit == null) {
                    profit = BigDecimal.ZERO;
                }
                return profit.subtract(costs).subtract(fees).setScale(2, RoundingMode.HALF_UP);

            case TRANSFER_WITHDRAWAL:
                return costs.negate().setScale(2, RoundingMode.HALF_UP);

            case TRANSFER_DEPOSIT:
                return currentValue.setScale(2, RoundingMode.HALF_UP);

            default:
                return BigDecimal.ZERO;
        }
    }

    /**
     * Перевіряє та повертає коректну дату створення транзакції.
     *
     * @param createdAt дата створення для перевірки.
     * @return перевірена дата створення. Якщо вхідна дата є null або знаходиться в майбутньому,
     * додається помилка у список помилок.
     */
    private LocalDateTime validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null || createdAt.isAfter(LocalDateTime.now())) {
            errors.add("Дата створення не може бути в майбутньому або пустою.");
        }
        return createdAt;
    }

    /**
     * Повертає витрати, пов'язані з транзакцією.
     *
     * @return значення витрат.
     */
    public BigDecimal getCosts() {
        return costs;
    }

    /**
     * Встановлює витрати для транзакції.
     *
     * @param costs значення витрат. Якщо значення null або менше 0, додається помилка у список
     *              помилок.
     */
    public void setCosts(BigDecimal costs) {
        if (costs == null || costs.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Витрати не можуть бути від'ємними.");
        }
        this.costs = costs;
    }

    /**
     * Повертає кількість криптовалюти в транзакції.
     *
     * @return кількість криптовалюти.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Встановлює кількість криптовалюти для транзакції.
     *
     * @param amount кількість криптовалюти. Якщо значення null або менше або дорівнює 0, додається
     *               помилка у список помилок.
     */
    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Кількість монет має бути більше 0.");
        } else {
            this.amount = amount;
        }
    }

    /**
     * Повертає прибуток, отриманий від транзакції.
     *
     * @return значення прибутку.
     */
    public BigDecimal getProfit() {
        return profit;
    }

    /**
     * Встановлює прибуток для транзакції.
     *
     * @param profit значення прибутку. Якщо значення менше 0, додається помилка у список помилок.
     */
    public void setProfit(BigDecimal profit) {
        if (profit != null && profit.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Прибуток не може бути від’ємним.");
        }
        this.profit = profit;
    }

    /**
     * Повертає комісію, сплачену за транзакцію.
     *
     * @return значення комісії.
     */
    public BigDecimal getFees() {
        return fees;
    }

    /**
     * Встановлює комісію для транзакції.
     *
     * @param fees значення комісії. Якщо значення null або менше 0, додається помилка у список
     *             помилок.
     */
    public void setFees(BigDecimal fees) {
        if (fees == null || fees.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Комісії не можуть бути від'ємними.");
        }
        this.fees = fees;
    }

    /**
     * Повертає опис транзакції.
     *
     * @return опис транзакції.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Встановлює опис транзакції.
     *
     * @param description новий опис транзакції.
     */
    public void setDescription(String description) {
        if (description != null && description.length() > 256) {
            errors.add("Опис не має бути довшим за 256 символів.");
        }
        this.description = description != null ? description.trim() : null;
    }

    /**
     * Повертає дату та час створення транзакції.
     *
     * @return дата створення транзакції.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Встановлює дату створення транзакції.
     *
     * @param createdAt дата створення.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = validateCreatedAt(createdAt);
    }

    /**
     * Повертає ідентифікатор портфеля.
     *
     * @return ідентифікатор портфеля.
     */
    public UUID getPortfolioId() {
        return portfolioId;
    }

    /**
     * Встановлює ідентифікатор портфеля.
     *
     * @param portfolioId новий ідентифікатор портфеля.
     */
    public void setPortfolioId(UUID portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * Повертає тип транзакції.
     *
     * @return тип транзакції.
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * Встановлює тип транзакції.
     *
     * @param transactionType тип транзакції (наприклад, купівля, продаж тощо). Якщо значення null
     *                        або не входить у перелік підтримуваних типів, додається помилка у
     *                        список помилок.
     */
    public void setTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            errors.add("Тип транзакції не може бути порожнім.");
        } else if (!EnumSet.allOf(TransactionType.class).contains(transactionType)) {
            errors.add("Такого типу транзакції не існує.");
        } else {
            this.transactionType = transactionType;
        }
    }

    /**
     * Повертає строкове представлення транзакції.
     *
     * @return строкове представлення.
     */
    @Override
    public String toString() {
        return "Transaction{" +
            "cryptocurrency=" + (cryptocurrency != null ? cryptocurrency.getName() : "null") +
            ", transactionType=" + transactionType +
            ", amount=" + amount +
            ", costs=" + costs +
            ", profit=" + profit +
            ", fees=" + fees +
            ", description='" + description + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}
