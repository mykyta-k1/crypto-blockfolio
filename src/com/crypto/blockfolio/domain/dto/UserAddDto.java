package com.crypto.blockfolio.domain.dto;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import com.crypto.blockfolio.persistence.exception.EntityArgumentException;
import com.crypto.blockfolio.persistence.validation.ValidationUtils;
import java.util.UUID;

public class UserAddDto extends Entity {

    private final String username;
    private final String rawPassword;
    private final String email;

    public UserAddDto(UUID id, String username, String rawPassword, String email) {
        super(id);
        this.username = validateUsername(username);
        this.rawPassword = validatePassword(rawPassword);
        this.email = validateEmail(email);

        if (!errors.isEmpty()) {
            throw new EntityArgumentException(errors);
        }
    }

    private String validateUsername(String username) {
        ValidationUtils.validateRequired(username, "логіну", errors);
        ValidationUtils.validateLength(username, 4, 24, "логіну", errors);
        ValidationUtils.validatePattern(username, "^[a-zA-Z0-9_]+$", "логіну", errors);
        return username;
    }

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

    private String validateEmail(String email) {
        ValidationUtils.validateRequired(email, "електронної пошти", errors);
        ValidationUtils.validatePattern(email, "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
            "електронної пошти", errors);
        return email;
    }

    public String username() {
        return username;
    }

    public String rawPassword() {
        return rawPassword;
    }

    public String email() {
        return email;
    }
}
