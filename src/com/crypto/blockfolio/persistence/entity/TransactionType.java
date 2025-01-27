package com.crypto.blockfolio.persistence.entity;

/**
 * Перелічення типів транзакцій, які підтримує система. Визначає різні операції, що можуть бути
 * виконані з криптовалютами.
 */
public enum TransactionType {

    /**
     * Купівля криптовалюти.
     */
    BUY,

    /**
     * Продаж криптовалюти.
     */
    SELL,

    /**
     * Переказ або виведення криптовалюти.
     */
    TRANSFER_WITHDRAWAL,

    /**
     * Переказ або депозит криптовалюти.
     */
    TRANSFER_DEPOSIT
}

