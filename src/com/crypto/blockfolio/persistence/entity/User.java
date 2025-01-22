package com.crypto.blockfolio.persistence.entity;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class User extends Entity implements Comparable<User> {

    private static final int MAX_PORTFOLIOS = 10;

    private final String password;
    private final LocalDateTime createdAt;
    private final List<Portfolio> portfolios;
    private String email;
    private String username;

    public User(UUID id, String password, String username, String email) {
        super(id);
        this.password = validatedPassword(password);
        setEmail(email);
        setUsername(username);
        this.createdAt = LocalDateTime.now();
        this.portfolios = new ArrayList<>();

        if (!this.isValid()) {
            throw new EntityArgumentException(errors);
        }
    }

    public void addPortfolio(Portfolio portfolio) {
        if (portfolios.size() >= MAX_PORTFOLIOS) {
            errors.add("Користувач не може мати більше ніж " + MAX_PORTFOLIOS + " портфелів.");
        }
        if (portfolio == null) {
            errors.add("Портфель не може бути null.");
        }
        if (!errors.isEmpty()) {
            return;
        }
        portfolios.add(portfolio);
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

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        if (portfolios != null && portfolios.size() > MAX_PORTFOLIOS) {
            errors.add("Список портфелів перевищує максимальну кількість " + MAX_PORTFOLIOS);
        }
        if (this.isValid()) {
            return;
        }
        this.portfolios.clear();
        if (portfolios != null) {
            this.portfolios.addAll(portfolios);
        }
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
}
