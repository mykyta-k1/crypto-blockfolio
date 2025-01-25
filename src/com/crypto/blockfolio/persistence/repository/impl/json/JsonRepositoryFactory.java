package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.Identifiable;
import com.crypto.blockfolio.persistence.exception.JsonFileIOException;
import com.crypto.blockfolio.persistence.repository.RepositoryFactory;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.TransactionRepository;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class JsonRepositoryFactory extends RepositoryFactory {

    private final Gson gson;
    private final UserJsonRepositoryImpl userJsonRepositoryImpl;
    private final CryptocurrencyJsonRepositoryImpl cryptocurrencyJsonRepositoryImpl;
    private final PortfolioJsonRepositoryImpl portfolioJsonRepositoryImpl;
    private final TransactionJsonRepositoryImpl transactionJsonRepositoryImpl;

    private JsonRepositoryFactory() {
        // Налаштування Gson
        GsonBuilder gsonBuilder = new GsonBuilder();

        // Реєстрація адаптерів
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
            @Override
            public void write(JsonWriter out, LocalDateTime value) throws IOException {
                out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            @Override
            public LocalDateTime read(JsonReader in) throws IOException {
                return LocalDateTime.parse(in.nextString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        });

        gsonBuilder.registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
            @Override
            public void write(JsonWriter out, LocalDate value) throws IOException {
                out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }

            @Override
            public LocalDate read(JsonReader in) throws IOException {
                return LocalDate.parse(in.nextString(), DateTimeFormatter.ISO_LOCAL_DATE);
            }
        });

        gson = gsonBuilder.setPrettyPrinting().create();

        // Ініціалізація репозиторіїв
        cryptocurrencyJsonRepositoryImpl = new CryptocurrencyJsonRepositoryImpl(gson);
        portfolioJsonRepositoryImpl = new PortfolioJsonRepositoryImpl(gson);
        transactionJsonRepositoryImpl = new TransactionJsonRepositoryImpl(gson);
        userJsonRepositoryImpl = new UserJsonRepositoryImpl(gson);
    }

    public static JsonRepositoryFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public CryptocurrencyRepository getCryptocurrencyRepository() {
        return cryptocurrencyJsonRepositoryImpl;
    }

    @Override
    public PortfolioRepository getPortfolioRepository() {
        return portfolioJsonRepositoryImpl;
    }

    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionJsonRepositoryImpl;
    }

    @Override
    public UserRepository getUserRepository() {
        return userJsonRepositoryImpl;
    }

    public void commit() {
        serializeEntities(userJsonRepositoryImpl.getPath(), userJsonRepositoryImpl.findAll());
        serializeEntities(cryptocurrencyJsonRepositoryImpl.getPath(),
            cryptocurrencyJsonRepositoryImpl.findAll());
        serializeEntities(portfolioJsonRepositoryImpl.getPath(),
            portfolioJsonRepositoryImpl.findAll());
        serializeEntities(transactionJsonRepositoryImpl.getPath(),
            transactionJsonRepositoryImpl.findAll());
    }

    private <E extends Identifiable<ID>, ID> void serializeEntities(Path path, Set<E> entities) {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            // Скидуємо файл перед збереженням
            writer.write("");
            // Перетворення колекції у JSON та запис у файл
            gson.toJson(entities, writer);
        } catch (IOException e) {
            throw new JsonFileIOException("Не вдалося зберегти дані у json-файл. Детальніше: %s"
                .formatted(e.getMessage()));
        }
    }

    private static class InstanceHolder {

        public static final JsonRepositoryFactory INSTANCE = new JsonRepositoryFactory();
    }
}
