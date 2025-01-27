package com.crypto.blockfolio.persistence.validation;

import com.crypto.blockfolio.persistence.entity.ErrorTemplates;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Утилітарний клас для валідації даних. Забезпечує перевірки на обов'язковість, довжину,
 * відповідність шаблону та додатність чисел.
 */
public class ValidationUtils {

    /**
     * Перевіряє, чи є значення обов'язковим.
     *
     * @param value     значення для перевірки.
     * @param fieldName назва поля (використовується для повідомлення про помилки).
     * @param errors    набір для зберігання повідомлень про помилки.
     */
    public static void validateRequired(String value, String fieldName, Set<String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.add(ErrorTemplates.REQUIRED.getTemplate().formatted(fieldName));
        }
    }

    /**
     * Перевіряє, чи відповідає довжина значення заданим обмеженням.
     *
     * @param value     значення для перевірки.
     * @param min       мінімальна довжина.
     * @param max       максимальна довжина.
     * @param fieldName назва поля (використовується для повідомлення про помилки).
     * @param errors    набір для зберігання повідомлень про помилки.
     */
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

    /**
     * Перевіряє, чи відповідає значення заданому регулярному виразу.
     *
     * @param value     значення для перевірки.
     * @param regex     регулярний вираз.
     * @param fieldName назва поля (використовується для повідомлення про помилки).
     * @param errors    набір для зберігання повідомлень про помилки.
     */
    public static void validatePattern(String value, String regex, String fieldName,
        Set<String> errors) {
        if (value != null) {
            var pattern = Pattern.compile(regex);
            if (!pattern.matcher(value).matches()) {
                errors.add(ErrorTemplates.ONLY_LATIN.getTemplate().formatted(fieldName));
            }
        }
    }

    /**
     * Перевіряє, чи є число додатним.
     *
     * @param value     значення для перевірки.
     * @param fieldName назва поля (використовується для повідомлення про помилки).
     * @param errors    набір для зберігання повідомлень про помилки.
     */
    public static void validatePositiveNumber(Number value, String fieldName, Set<String> errors) {
        if (value == null || value.doubleValue() <= 0) {
            errors.add("Значення %s має бути додатним.".formatted(fieldName));
        }
    }
}
