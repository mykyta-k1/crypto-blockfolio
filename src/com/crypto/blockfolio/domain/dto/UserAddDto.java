package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) для створення користувача. Використовується для передачі даних,
 * необхідних для створення користувача в системі.
 */
public final class UserAddDto extends Entity {

    private final String username;
    private final String rawPassword;
    private final String email;
    private final Set<UUID> portfolios;

    /**
     * Конструктор для створення нового екземпляра {@link UserAddDto}.
     *
     * @param id          унікальний ідентифікатор користувача.
     * @param username    логін користувача.
     * @param rawPassword незашифрований пароль користувача.
     * @param email       електронна пошта користувача.
     * @param portfolios  набір ідентифікаторів портфелів, які належать користувачу.
     * @throws EntityArgumentException якщо вхідні дані некоректні.
     */
    public UserAddDto(UUID id, String username, String rawPassword, String email,
        Set<UUID> portfolios) {
        super(id);
        this.username = validateUsername(username);
        this.rawPassword = validatePassword(rawPassword);
        this.email = validateEmail(email);
        this.portfolios = validatePortfolios(portfolios);

        if (!errors.isEmpty()) {
            throw new EntityArgumentException(errors);
        }
    }

    /**
     * Перевіряє та повертає логін користувача.
     *
     * @param username логін користувача для перевірки.
     * @return перевірений логін.
     */
    private String validateUsername(String username) {
        ValidationUtils.validateRequired(username, "логіну", errors);
        ValidationUtils.validateLength(username, 4, 24, "логіну", errors);
        ValidationUtils.validatePattern(username, "^[a-zA-Z0-9_]+$", "логіну", errors);
        return username;
    }

    /**
     * Перевіряє та повертає незашифрований пароль користувача.
     *
     * @param password пароль користувача для перевірки.
     * @return перевірений пароль.
     */
    private String validatePassword(String password) {
        ValidationUtils.validateRequired(password, "паролю", errors);
        if (password != null && password.length() < 8) {
            errors.add(ErrorTemplates.MIN_LENGTH.getTemplate().formatted("паролю", 8));
        }
        if (password != null && !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$")) {
            errors.add(ErrorTemplates.PASSWORD.getTemplate().formatted("паролю"));
        }
        return password;
    }

    /**
     * Перевіряє та повертає електронну пошту користувача.
     *
     * @param email електронна пошта для перевірки.
     * @return перевірена електронна пошта.
     */
    private String validateEmail(String email) {
        ValidationUtils.validateRequired(email, "електронної пошти", errors);
        ValidationUtils.validatePattern(email, "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
            "електронної пошти", errors);
        return email;
    }


    /**
     * Перевіряє та повертає набір ідентифікаторів портфелів.
     *
     * @param portfolios набір портфелів для перевірки.
     * @return перевірений набір портфелів.
     */
    private Set<UUID> validatePortfolios(Set<UUID> portfolios) {
        if (portfolios == null) {
            return new LinkedHashSet<>();
        }
        if (portfolios.size() > 10) {
            errors.add("Користувач не може мати більше ніж 10 портфелів.");
        }
        return new LinkedHashSet<>(portfolios);
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
     * Повертає незашифрований пароль користувача.
     *
     * @return незашифрований пароль.
     */
    public String getRawPassword() {
        return rawPassword;
    }

    /**
     * Повертає електронну пошту користувача.
     *
     * @return електронна пошта користувача.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Повертає набір ідентифікаторів портфелів користувача.
     *
     * @return набір ідентифікаторів портфелів.
     */
    public Set<UUID> getPortfolios() {
        return portfolios;
    }
}
