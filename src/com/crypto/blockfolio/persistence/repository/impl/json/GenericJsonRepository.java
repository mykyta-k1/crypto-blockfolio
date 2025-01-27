package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.Identifiable;
import com.crypto.blockfolio.persistence.exception.JsonFileIOException;
import com.crypto.blockfolio.persistence.repository.Repository;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Загальний клас репозиторію для роботи з об'єктами, що реалізують інтерфейс {@link Identifiable}.
 * Репозиторій забезпечує функції збереження, завантаження, пошуку та видалення об'єктів у форматі
 * JSON.
 *
 * @param <E>  тип об'єкта, що зберігається у репозиторії.
 * @param <ID> тип ідентифікатора об'єкта.
 */
class GenericJsonRepository<E extends Identifiable<ID>, ID> implements Repository<E, ID> {

    /**
     * Колекція збережених об'єктів.
     */
    protected final Set<E> entities;

    /**
     * Функція для вилучення ідентифікатора з об'єкта.
     */
    private final Function<E, ID> identifierExtractor;

    /**
     * Об'єкт для роботи з JSON.
     */
    private final Gson gson;

    /**
     * Шлях до файлу, де зберігаються дані.
     */
    private final Path path;

    /**
     * Тип колекції, що використовується для десеріалізації JSON.
     */
    private final Type collectionType;

    /**
     * Конструктор, який ініціалізує репозиторій.
     *
     * @param gson                об'єкт для серіалізації та десеріалізації JSON.
     * @param path                шлях до файлу, де зберігаються дані.
     * @param collectionType      тип колекції, яка використовується для зберігання об'єктів.
     * @param identifierExtractor функція для отримання ідентифікатора з об'єкта.
     */
    GenericJsonRepository(Gson gson, Path path, Type collectionType,
        Function<E, ID> identifierExtractor) {
        this.gson = gson;
        this.path = path;
        this.collectionType = collectionType;
        entities = loadAll();
        this.identifierExtractor = identifierExtractor;
    }

    /**
     * Знаходить об'єкт за його ідентифікатором.
     *
     * @param id ідентифікатор об'єкта.
     * @return {@link Optional}, що містить об'єкт, якщо він знайдений.
     */
    @Override
    public Optional<E> findById(ID id) {
        return entities.stream()
            .filter(e -> identifierExtractor.apply(e).equals(id))
            .findFirst();
    }

    /**
     * Повертає всі об'єкти з репозиторію.
     *
     * @return набір всіх об'єктів.
     */
    @Override
    public Set<E> findAll() {
        return entities;
    }

    /**
     * Повертає всі об'єкти, що відповідають заданому фільтру.
     *
     * @param filter предикат для фільтрації об'єктів.
     * @return набір об'єктів, що відповідають фільтру.
     */
    @Override
    public Set<E> findAll(Predicate<E> filter) {
        return entities.stream().filter(filter).collect(Collectors.toSet());
    }

    /**
     * Додає новий об'єкт у репозиторій. Якщо об'єкт вже існує, він замінюється.
     *
     * @param entity об'єкт для додавання.
     * @return доданий об'єкт.
     */
    @Override
    public E add(E entity) {
        entities.remove(entity);
        entities.add(entity);
        saveChanges();
        return entity;
    }

    /**
     * Видаляє об'єкт із репозиторію.
     *
     * @param entity об'єкт для видалення.
     * @return {@code true}, якщо об'єкт успішно видалено.
     */
    @Override
    public boolean remove(E entity) {
        boolean removed = entities.remove(entity);
        if (removed) {
            saveChanges();
        }
        return removed;
    }

    /**
     * Повертає шлях до файлу репозиторію.
     *
     * @return шлях до файлу.
     */
    public Path getPath() {
        return path;
    }

    /**
     * Завантажує всі об'єкти з файлу JSON.
     *
     * @return набір об'єктів.
     */
    private Set<E> loadAll() {
        try {
            fileNotFound();
            var json = Files.readString(path);
            return isValidJson(json) ? gson.fromJson(json, collectionType) : new HashSet<>();
        } catch (IOException e) {
            throw new JsonFileIOException("Помилка при роботі із файлом %s."
                .formatted(path.getFileName()));
        }
    }

    /**
     * Зберігає всі зміни у файл JSON.
     */
    protected void saveChanges() {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            gson.toJson(entities, writer);
            //System.out.println("Дані збережено у файл: " + path.toAbsolutePath());
        } catch (IOException e) {
            throw new JsonFileIOException(
                "Не вдалося зберегти зміни у файл: %s".formatted(path.getFileName()), e);
        }
    }

    /**
     * Перевіряє, чи є JSON-рядок валідним.
     *
     * @param input JSON у вигляді рядка.
     * @return {@code true}, якщо JSON валідний.
     */
    private boolean isValidJson(String input) {
        try (JsonReader reader = new JsonReader(new StringReader(input))) {
            reader.skipValue();
            return reader.peek() == JsonToken.END_DOCUMENT;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Перевіряє, чи існує файл. Якщо файл не існує, він створюється.
     *
     * @throws IOException у разі помилки створення файлу.
     */
    private void fileNotFound() throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }
}
