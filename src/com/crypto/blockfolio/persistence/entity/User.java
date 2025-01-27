package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Клас, що представляє користувача системи. Включає дані про логін, пароль, електронну пошту,
 * створені портфелі та дату реєстрації. Також забезпечує управління портфелями та транзакціями.
 */
public class User extends Entity implements Comparable<User> {

    /**
     * Максимальна кількість портфелів, які може мати користувач.
     */
    private static final int MAX_PORTFOLIOS = 10;
    /**
     * Зашифрований пароль користувача.
     */
    private final String password;
    /**
     * Дата та час створення облікового запису користувача.
     */
    private final LocalDateTime createdAt;
    /**
     * Логін користувача.
     */
    private String username;
    /**
     * Електронна пошта користувача.
     */
    private String email;
    /**
     * Список портфелів користувача (за їх унікальними ID).
     */
    private Set<UUID> portfolios = new LinkedHashSet<>();

    /**
     * Конструктор для створення нового об'єкта {@link User}.
     *
     * @param id       унікальний ідентифікатор користувача.
     * @param password пароль користувача.
     * @param username логін користувача.
     * @param email    електронна пошта користувача.
     * @throws EntityArgumentException якщо вхідні дані некоректні.
     */
    public User(UUID id, String password, String username, String email) {
        super(id);
        this.password = validatedPassword(password);
        setEmail(email);
        setUsername(username);
        this.createdAt = LocalDateTime.now();
        this.portfolios = new LinkedHashSet<>();

        if (!this.isValid()) {
            throw new EntityArgumentException(errors);
        }
    }

    /**
     * Додає транзакцію до портфеля користувача.
     *
     * @param portfolioId           ідентифікатор портфеля.
     * @param transactionId         ідентифікатор транзакції.
     * @param portfolioRepository   репозиторій портфелів.
     * @param transactionRepository репозиторій транзакцій.
     * @throws IllegalArgumentException якщо портфель або транзакція не належать користувачу.
     */
    public void addTransactionToPortfolio(UUID portfolioId, UUID transactionId,
        PortfolioRepository portfolioRepository, TransactionRepository transactionRepository) {

        if (!portfolios.contains(portfolioId)) {
            throw new IllegalArgumentException("Цей портфель не належить даному користувачу.");
        }

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Портфель із ID " + portfolioId + " не знайдено."));

        transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Транзакція із ID " + transactionId + " не знайдена."));

        portfolioRepository.addTransaction(portfolioId, transactionId);
    }

    /**
     * Видаляє портфель із списку користувача.
     *
     * @param portfolio об'єкт портфеля для видалення.
     * @return {@code true}, якщо портфель успішно видалено, інакше {@code false}.
     */
    public boolean removePortfolio(Portfolio portfolio) {
        if (portfolio == null || !portfolios.remove(portfolio)) {
            errors.add("Портфель не знайдено у списку.");
            return false;
        }
        return true;
    }

    /**
     * Перевіряє коректність пароля користувача.
     *
     * @param password пароль для перевірки.
     * @return перевірений пароль.
     */
    private String validatedPassword(String password) {
        final String templateName = "пароля";

        if (password == null || password.trim().isEmpty()) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted(templateName));
        }
        if (password.length() < 8) {
            errors.add(ErrorTemplates.MIN_LENGTH.getTemplate().formatted(templateName, 8));
        }
        var pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
        if (!pattern.matcher(password).matches()) {
            errors.add(ErrorTemplates.PASSWORD.getTemplate().formatted(templateName));
        }

        return password;
    }

    /**
     * Порівнює користувачів за їх логіном.
     *
     * @param o інший користувач.
     * @return результат порівняння.
     */
    @Override
    public int compareTo(User o) {
        return this.username.compareTo(o.username);
    }

    /**
     * Повертає зашифрований пароль користувача.
     *
     * @return пароль користувача.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Повертає логін користувача.
     *
     * @return логін користувача.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Встановлює логін користувача.
     *
     * @param username новий логін.
     */
    public void setUsername(String username) {
        final String templateName = "логіну";
        username = username != null ? username.trim() : null;
        ValidationUtils.validateRequired(username, templateName, errors);
        ValidationUtils.validateLength(username, 4, 24, templateName, errors);
        ValidationUtils.validatePattern(username, "^[a-zA-Z0-9_]+$", templateName, errors);
        this.username = username;
    }

    /**
     * Повертає дату створення облікового запису.
     *
     * @return дата створення.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Повертає список портфелів користувача.
     *
     * @return набір ідентифікаторів портфелів.
     */
    public Set<UUID> getPortfolios() {
        return portfolios;
    }

    /**
     * Встановлює список портфелів користувача.
     *
     * @param portfolios новий набір ідентифікаторів портфелів.
     */
    public void setPortfolios(Set<UUID> portfolios) {
        this.portfolios =
            portfolios != null ? new LinkedHashSet<>(portfolios) : new LinkedHashSet<>();
    }

    /**
     * Повертає електронну пошту користувача.
     *
     * @return електронна пошта.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Встановлює електронну пошту користувача.
     *
     * @param email нова електронна пошта.
     */
    public void setEmail(String email) {
        final String templateName = "електронної пошти";
        email = email != null ? email.trim() : null;
        ValidationUtils.validateRequired(email, templateName, errors);
        ValidationUtils.validatePattern(email, "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
            templateName, errors);
        this.email = email;
    }

    /**
     * Додає портфель до користувача.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return {@code true}, якщо портфель успішно додано, інакше {@code false}.
     */
    public boolean addPortfolio(UUID portfolioId) {
        if (portfolioId == null) {
            errors.add("ID портфеля не може бути null.");
            return false;
        }

        if (portfolios.contains(portfolioId)) {
            errors.add("Портфель із цим ID вже існує у користувача.");
            return false;
        }

        portfolios.add(portfolioId);
        return true;
    }

    /**
     * Видаляє портфель користувача за його ID.
     *
     * @param portfolioId ідентифікатор портфеля.
     * @return {@code true}, якщо портфель успішно видалено, інакше {@code false}.
     */
    public boolean removePortfolio(UUID portfolioId) {
        if (portfolioId == null || !portfolios.contains(portfolioId)) {
            errors.add("Портфель із таким ID не знайдено у користувача.");
            return false;
        }

        portfolios.remove(portfolioId);
        return true;
    }

    /**
     * Повертає строкове представлення користувача.
     *
     * @return строкове представлення користувача.
     */
    @Override
    public String toString() {
        return "User{" +
            "password='" + password + '\'' +
            ", email='" + email + '\'' +
            ", username='" + username + '\'' +
            ", createdAt='" + createdAt + '\'' +
            '}';
    }
}
