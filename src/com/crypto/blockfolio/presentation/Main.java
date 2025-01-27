package com.crypto.blockfolio.presentation;


import com.crypto.blockfolio.presentation.pages.DashBoardView;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

/**
 * Головний клас програми. Відповідає за ініціалізацію контексту застосунку та запуск головного
 * інтерфейсу користувача (панелі приладів).
 */
public class Main {

    /**
     * Точка входу в програму.
     *
     * @param args аргументи командного рядка.
     * @throws NotImplementedException якщо контекст або залежності не реалізовані.
     */
    public static void main(String[] args) throws NotImplementedException {
        // Ініціалізація контексту застосунку
        ApplicationContext applicationContext = ApplicationContext.getInstance();
        // Відображення головної панелі приладів
        DashBoardView dashBoardView = new DashBoardView();
        dashBoardView.display();
    }
}
