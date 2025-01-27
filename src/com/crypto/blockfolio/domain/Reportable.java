package com.crypto.blockfolio.domain;

import java.util.function.Predicate;

/**
 * Інтерфейс, що визначає контракт для створення звітів. Дозволяє генерувати звіти на основі
 * заданого предикату.
 *
 * @param <E> тип елементів, для яких генерується звіт.
 */
public interface Reportable<E> {

    /**
     * Директорія за замовчуванням, у якій зберігаються звіти.
     */
    String REPORTS_DIRECTORY = "Reports";

    /**
     * Генерує звіт для елементів, що відповідають заданому предикату.
     *
     * @param predicate предикат, що визначає, які елементи повинні бути включені у звіт.
     */
    void generateReport(Predicate<E> predicate);
}

