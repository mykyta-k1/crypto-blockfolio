package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class Portfolio extends Entity implements Comparable<Portfolio> {

    private final UUID ownerId;
    private final LocalDateTime createdAt;
    private String name;
    private BigDecimal totalValue;
    private Set<Cryptocurrency> watchlist;
    private Set<Transaction> transactionsList;

    public Portfolio(UUID id, UUID ownerId, String name,
        Set<Cryptocurrency> watchlist, Set<Transaction> transactions) {
        super(id);
        this.ownerId = ownerId;
        this.name = name;
        this.totalValue = BigDecimal.ZERO;
        this.watchlist = watchlist != null ? new LinkedHashSet<>(watchlist) : new LinkedHashSet<>();
        this.transactionsList =
            transactions != null ? new LinkedHashSet<>(transactions) : new LinkedHashSet<>();
        this.createdAt = LocalDateTime.now();

        if (!this.isValid()) {
            throw new EntityArgumentException(errors);
        }
    }

    public void addToWatchlist(Cryptocurrency cryptocurrency) {
        errors.clear();
        if (cryptocurrency == null || !cryptocurrency.isValid()) {
            errors.add("Вибрана криптовалюта не є валідною.");
            return;
        }
        if (!watchlist.add(cryptocurrency)) {
            errors.add("Вибрана криптовалюта вже є у портфелі.");
        } else {
            calculateTotalValue();
        }
    }

    public void delFromWatchlist(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || !watchlist.remove(cryptocurrency)) {
            errors.add("Вибрана криптовалюта не знайдена у портфелі.");
        } else {
            calculateTotalValue();
        }
    }

    public void addTransactions(Transaction transaction) {
        errors.clear();
        if (transaction == null) {
            errors.add("Транзакція не може бути null.");
            return;
        }
        if (!transaction.isValid()) {
            errors.add("Транзакція має помилки: " + transaction.getErrors());
            return;
        }
        if (!transactionsList.add(transaction)) {
            errors.add("Така транзакція вже існує.");
        }
    }

    public void delTransactions(Transaction transaction) {
        if (transaction == null || !transactionsList.remove(transaction)) {
            errors.add("Така транзакція не знайдена.");
        }
        calculateTotalValue();
    }

    private LocalDateTime validateCreateAt(LocalDateTime createAt) {
        if (createAt == null || createAt.isAfter(LocalDateTime.now())) {
            errors.add("Дата створення не може бути в майбутньому або пустою.");
        }

        return createAt;
    }

    public void calculateTotalValue() {
        if (watchlist == null || watchlist.isEmpty()) {
            this.totalValue = BigDecimal.ZERO;
            return;
        }

        this.totalValue = watchlist.stream()
            .map(crypto -> BigDecimal.valueOf(crypto.getCurrentPrice())
                .multiply(BigDecimal.valueOf(crypto.getCount())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public int compareTo(Portfolio o) {
        return this.name.compareTo(o.name);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Transaction> getTransactionsList() {
        return transactionsList;
    }

    public void setTransactionsList(Set<Transaction> transactionsList) {
        errors.clear();
        if (transactionsList == null) {
            errors.add("Список транзакцій не може бути null.");
            return;
        }

        for (Transaction transaction : transactionsList) {
            if (transaction == null || !transaction.isValid()) {
                errors.add("Список транзакцій містить некоректну транзакцію: " +
                    (transaction != null ? transaction.getErrors() : "null"));
                return;
            }
        }

        this.transactionsList = new LinkedHashSet<>(transactionsList);
        calculateTotalValue();
    }

    public void setTransactions(Set<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
    }

    public Set<Cryptocurrency> getWatchlist() {
        return watchlist;
    }

    public void setWatchlist(Set<Cryptocurrency> watchlist) {
        this.watchlist = watchlist;
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
        ValidationUtils.validatePattern(name, "^[a-zA-Z0-9_]+$", templateName, errors);
        this.name = name;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        errors.clear();
        if (totalValue == null || totalValue.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Загальна вартість не може бути null або менше 0.");
            return;
        }
        this.totalValue = totalValue;
    }

    @Override
    public String toString() {
        return "Portfolio{" +
            "name='" + name + '\'' +
            ", totalValue=" + totalValue +
            ", owner=" + ownerId +
            ", createdAt=" + createdAt +
            ", watchlist=" + watchlist +
            ", transactionsList=" + transactionsList +
            '}';
    }
}
