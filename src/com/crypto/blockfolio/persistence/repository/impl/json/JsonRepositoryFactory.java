package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.Entity;
import com.crypto.blockfolio.persistence.exception.JsonFileIOException;
import com.crypto.blockfolio.persistence.repository.RepositoryFactory;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.crypto.blockfolio.persistence.repository.contracts.PortfolioRepository;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;

public class JsonRepositoryFactory extends RepositoryFactory {

    private final Gson gson;
    private final UserJsonRepositoryImpl userJsonRepositoryImpl;
    private final CryptocurrencyJsonRepositoryImpl cryptocurrencyJsonRepositoryImpl;
    private final PortfolioJsonRepositoryImpl portfolioJsonRepositoryImpl;
    //private final TransactionJsonRepositoryImpl transactionJsonRepositoryImpl;

    private JsonRepositoryFactory() {
        // Адаптер для типу даних LocalDateTime при серіалізації/десеріалізації
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
            (JsonSerializer<LocalDateTime>) (localDate, srcType, context) ->
                new JsonPrimitive(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm").format(localDate)));
        gsonBuilder.registerTypeAdapter(LocalDateTime.class,
            (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                LocalDateTime.parse(json.getAsString(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                        .withLocale(Locale.of("uk", "UA"))));

        // Адаптер для типу даних LocalDate при серіалізації/десеріалізації
        gsonBuilder.registerTypeAdapter(LocalDate.class,
            (JsonSerializer<LocalDate>) (localDate, srcType, context) ->
                new JsonPrimitive(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy").format(localDate)));
        gsonBuilder.registerTypeAdapter(LocalDate.class,
            (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                LocalDate.parse(json.getAsString(),
                    DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        .withLocale(Locale.of("uk", "UA"))));

        gson = gsonBuilder.setPrettyPrinting().create();

        userJsonRepositoryImpl = new UserJsonRepositoryImpl(gson);
        cryptocurrencyJsonRepositoryImpl = new CryptocurrencyJsonRepositoryImpl(gson);
        portfolioJsonRepositoryImpl = new PortfolioJsonRepositoryImpl(gson);
        //transactionJsonRepositoryImpl = new TransactionJsonRepositoryImpl(gson);
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

    /*
    @Override
    public TransactionRepository getTransactionRepository() {
        return transactionJsonRepositoryImpl;
    }
    */
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
        /*
        serializeEntities(transactionJsonRepositoryImpl.getPath(),
            transactionJsonRepositoryImpl.findAll()); */
    }

    private <E extends Entity> void serializeEntities(Path path, Set<E> entities) {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            // Скидуємо файлик, перед збереженням!
            writer.write("");
            // Перетворюємо колекцію користувачів в JSON та записуємо у файл
            gson.toJson(entities, writer);

        } catch (IOException e) {
            throw new JsonFileIOException("Не вдалось зберегти дані у json-файл. Детальніше: %s"
                .formatted(e.getMessage()));
        }
    }

    private static class InstanceHolder {

        public static final JsonRepositoryFactory INSTANCE = new JsonRepositoryFactory();
    }
}
