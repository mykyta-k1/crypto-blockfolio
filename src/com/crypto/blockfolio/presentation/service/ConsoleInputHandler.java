package com.crypto.blockfolio.presentation.service;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleInputHandler implements AutoCloseable {

    private final Scanner scanner;

    public ConsoleInputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        try {
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Ввід завершено або недоступний.");
            }
            return scanner.nextLine().trim();
        } catch (NoSuchElementException | IllegalStateException e) {
            throw new RuntimeException("Помилка під час зчитування даних: " + e.getMessage(), e);
        }
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Будь ласка, введіть коректне ціле число.");
            } catch (NoSuchElementException | IllegalStateException e) {
                throw new RuntimeException("Помилка під час зчитування даних: " + e.getMessage(),
                    e);
            }
        }
    }

    /**
     * Зчитує дійсне число з консолі після виведення підказки. Після зчитування очищує кеш сканера.
     *
     * @param prompt текст підказки
     * @return введене користувачем дійсне число
     */
    public double readDouble(String prompt) {
        System.out.print(prompt);
        double input = scanner.nextDouble();
        clearScannerCache();
        return input;
    }

    /**
     * Очищує кеш сканера для уникнення конфліктів із залишками в буфері.
     */
    private void clearScannerCache() {
        if (scanner.hasNextLine()) {
            scanner.nextLine(); // Читаємо залишки в буфері, якщо вони є
        }
    }

    /**
     * Закриває сканер після завершення роботи.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }

    }
}

