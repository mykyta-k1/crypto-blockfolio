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

public class User extends Entity implements Comparable<User> {

    private static final int MAX_PORTFOLIOS = 10;

    private final String password;
    private final LocalDateTime createdAt;
    private String username;
    private String email;
    private Set<UUID> portfolios = new LinkedHashSet<>();


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

    public void addTransactionToPortfolio(UUID portfolioId, UUID transactionId,
        PortfolioRepository portfolioRepository, TransactionRepository transactionRepository) {

        if (!portfolios.contains(portfolioId)) {
            throw new IllegalArgumentException("Цей портфель не належить даному користувачу.");
        }

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Портфель із ID " + portfolioId + " не знайдено."));

        // Перевірка на існування транзакції
        transactionRepository.findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Транзакція із ID " + transactionId + " не знайдена."));

        // Викликаємо метод у репозиторії портфоліо для додавання транзакції
        portfolioRepository.addTransaction(portfolioId, transactionId);
    }

    public boolean removePortfolio(Portfolio portfolio) {
        if (portfolio == null || !portfolios.remove(portfolio)) {
            errors.add("Портфель не знайдено у списку.");
            return false;
        }
        return true;
    }

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

    @Override
    public String toString() {
        return "User{" +
            "password='" + password + '\'' +
            ", email='" + email + '\'' +
            ", username='" + username + '\'' +
            ", createdAt='" + createdAt + '\'' +
            '}';
    }

    @Override
    public int compareTo(User o) {
        return this.username.compareTo(o.username);
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        final String templateName = "логіну";
        username = username != null ? username.trim() : null;
        ValidationUtils.validateRequired(username, templateName, errors);
        ValidationUtils.validateLength(username, 4, 24, templateName, errors);
        ValidationUtils.validatePattern(username, "^[a-zA-Z0-9_]+$", templateName, errors);
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<UUID> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(Set<UUID> portfolios) {
        this.portfolios =
            portfolios != null ? new LinkedHashSet<>(portfolios) : new LinkedHashSet<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        final String templateName = "електронної пошти";
        email = email != null ? email.trim() : null;
        ValidationUtils.validateRequired(email, templateName, errors);
        ValidationUtils.validatePattern(email, "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
            templateName, errors);
        this.email = email;
    }

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

    public boolean removePortfolio(UUID portfolioId) {
        if (portfolioId == null || !portfolios.contains(portfolioId)) {
            errors.add("Портфель із таким ID не знайдено у користувача.");
            return false;
        }

        portfolios.remove(portfolioId);
        return true;
    }

}
