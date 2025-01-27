package com.crypto.blockfolio.presentation;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * Інтерфейс для представлення візуальних елементів застосунку. Забезпечує базовий метод для
 * відображення вмісту.
 */
public interface ViewService {

    /**
     * Відображає візуальний компонент.
     *
     * @throws NotImplementedException якщо метод не реалізований у класі, що його викликає.
     */
    void display() throws NotImplementedException;
}

