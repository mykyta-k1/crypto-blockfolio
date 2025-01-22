package com.crypto.blockfolio.persistence.entity;

public enum ErrorTemplates {
    REQUIRED("Поле %s є обов'язковим до заповнення."),
    MIN_LENGTH("Поле %s не може бути меншим за %d симв."),
    MAX_LENGTH("Поле %s не може бути більшим за %d симв."),
    ONLY_LATIN("Поле %s має містити лише латинські символи та символ _."),
    PASSWORD(
        "Поле %s має містити латинські символи, де принаймні одна буква з великої та малої, і одна цифра.");

    private final String template;

    ErrorTemplates(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }
}
