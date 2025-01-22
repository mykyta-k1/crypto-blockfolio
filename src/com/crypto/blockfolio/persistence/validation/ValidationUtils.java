package com.crypto.blockfolio.persistence.validation;

import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import java.util.Set;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static void validateRequired(String value, String fieldName, Set<String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted(fieldName));
        }
    }

    public static void validateLength(String value, int min, int max, String fieldName,
        Set<String> errors) {
        if (value != null) {
            if (value.length() < min) {
                errors.add(ErrorTemplates.MIN_LENGTH.getTemplate().formatted(fieldName, min));
            }
            if (value.length() > max) {
                errors.add(ErrorTemplates.MAX_LENGTH.getTemplate().formatted(fieldName, max));
            }
        }
    }

    public static void validatePattern(String value, String regex, String fieldName,
        Set<String> errors) {
        if (value != null) {
            var pattern = Pattern.compile(regex);
            if (!pattern.matcher(value).matches()) {
                errors.add(ErrorTemplates.ONLY_LATIN.getTemplate().formatted(fieldName));
            }
        }
    }

    public static void validatePositiveNumber(Number value, String fieldName, Set<String> errors) {
        if (value == null || value.doubleValue() <= 0) {
            errors.add("Значення %s має бути додатним.".formatted(fieldName));
        }
    }
}
