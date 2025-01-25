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

class GenericJsonRepository<E extends Identifiable<ID>, ID> implements Repository<E, ID> {

    protected final Set<E> entities;
    private final Function<E, ID> identifierExtractor;
    private final Gson gson;
    private final Path path;
    private final Type collectionType;

    GenericJsonRepository(Gson gson, Path path, Type collectionType,
        Function<E, ID> identifierExtractor) {
        this.gson = gson;
        this.path = path;
        this.collectionType = collectionType;
        entities = loadAll();
        this.identifierExtractor = identifierExtractor;
    }

    @Override
    public Optional<E> findById(ID id) {
        return entities.stream()
            .filter(e -> identifierExtractor.apply(e).equals(id))
            .findFirst();
    }

    @Override
    public Set<E> findAll() {
        return entities;
    }

    @Override
    public Set<E> findAll(Predicate<E> filter) {
        return entities.stream().filter(filter).collect(Collectors.toSet());
    }

    @Override
    public E add(E entity) {
        entities.remove(entity);
        entities.add(entity);
        saveChanges();
        return entity;
    }

    @Override
    public boolean remove(E entity) {
        boolean removed = entities.remove(entity);
        if (removed) {
            saveChanges();
        }
        return removed;
    }

    public Path getPath() {
        return path;
    }

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
     * Метод для збереження змін, наприклад при оновленні цін криптомонеток
     */
    protected void saveChanges() {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            gson.toJson(entities, writer);
            System.out.println("Дані збережено у файл: " + path.toAbsolutePath());
        } catch (IOException e) {
            throw new JsonFileIOException(
                "Не вдалося зберегти зміни у файл: %s".formatted(path.getFileName()), e);
        }
    }

    /**
     * Перевірка на валідність формату даних JSON.
     *
     * @param input JSON у форматі рядка.
     * @return результат перевірки.
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
     * Якщо файлу не існує, то ми його створюємо.
     *
     * @throws IOException виключення при роботі із потоком вводу виводу.
     */
    private void fileNotFound() throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }
}
