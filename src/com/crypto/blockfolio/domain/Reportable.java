package com.crypto.blockfolio.domain;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    void generateReport(Path savePath, Predicate<E> predicate);

    /**
     * Визначає шлях для збереження звіту.
     *
     * @param userInputPath введений шлях користувачем.
     * @return підготовлений шлях для збереження.
     */
    default Path resolveSavePath(String userInputPath) {
        Path saveDirectory = userInputPath.isEmpty() ? Paths.get(REPORTS_DIRECTORY)
            : Paths.get(userInputPath);

        File directory = saveDirectory.toFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalArgumentException("Не вдалося створити директорію: " + saveDirectory);
        }
        return saveDirectory;
    }
}

