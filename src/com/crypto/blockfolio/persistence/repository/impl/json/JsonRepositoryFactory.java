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

/**
 * Фабрика для створення JSON-репозиторіїв. Забезпечує централізоване управління репозиторіями, їх
 * ініціалізацію та збереження змін у JSON-файли.
 */
public class JsonRepositoryFactory extends RepositoryFactory {

    /**
     * Об'єкт {@link Gson} для серіалізації та десеріалізації даних.
     */
    private final Gson gson;

    /**
     * Репозиторій користувачів.
     */
    private final UserJsonRepositoryImpl userJsonRepositoryImpl;

    /**
     * Репозиторій криптовалют.
     */
    private final CryptocurrencyJsonRepositoryImpl cryptocurrencyJsonRepositoryImpl;

    /**
     * Репозиторій портфелів.
     */
    private final PortfolioJsonRepositoryImpl portfolioJsonRepositoryImpl;

    /**
     * Репозиторій транзакцій.
     */
    private final TransactionJsonRepositoryImpl transactionJsonRepositoryImpl;

    /**
     * Репозиторій аутентифікаційних даних.
     */
    private final AuthDataRepository authDataRepository;

    /**
     * Приватний конструктор для ініціалізації фабрики. Використовує адаптери для роботи з типами
     * {@link LocalDateTime} та {@link LocalDate}.
     */
    private JsonRepositoryFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();

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

        cryptocurrencyJsonRepositoryImpl = new CryptocurrencyJsonRepositoryImpl(gson);
        portfolioJsonRepositoryImpl = new PortfolioJsonRepositoryImpl(gson);
        transactionJsonRepositoryImpl = new TransactionJsonRepositoryImpl(gson);
        userJsonRepositoryImpl = new UserJsonRepositoryImpl(gson);
        this.authDataRepository = new AuthDataRepository();
    }

    /**
     * Повертає єдиний екземпляр фабрики.
     *
     * @return екземпляр {@link JsonRepositoryFactory}.
     */
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

    @Override
    public AuthDataRepository getAuthDataRepository() {
        return authDataRepository;
    }

    /**
     * Зберігає всі зміни у файли JSON для всіх репозиторіїв.
     */
    public void commit() {
        serializeEntities(userJsonRepositoryImpl.getPath(), userJsonRepositoryImpl.findAll());
        serializeEntities(cryptocurrencyJsonRepositoryImpl.getPath(),
            cryptocurrencyJsonRepositoryImpl.findAll());
        serializeEntities(portfolioJsonRepositoryImpl.getPath(),
            portfolioJsonRepositoryImpl.findAll());
        serializeEntities(transactionJsonRepositoryImpl.getPath(),
            transactionJsonRepositoryImpl.findAll());
    }

    /**
     * Серіалізує колекцію об'єктів у JSON і зберігає у файл.
     *
     * @param path     шлях до файлу.
     * @param entities колекція об'єктів для серіалізації.
     * @param <E>      тип об'єктів.
     * @param <ID>     тип ідентифікатора об'єктів.
     */
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

    /**
     * Внутрішній клас для забезпечення Singleton фабрики.
     */
    private static class InstanceHolder {

        /**
         * Єдиний екземпляр фабрики {@link JsonRepositoryFactory}.
         */
        public static final JsonRepositoryFactory INSTANCE = new JsonRepositoryFactory();
    }
}
