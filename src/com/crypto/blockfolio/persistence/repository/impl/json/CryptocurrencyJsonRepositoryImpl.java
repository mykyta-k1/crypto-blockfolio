package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.Cryptocurrency;
import com.crypto.blockfolio.persistence.repository.contracts.CryptocurrencyRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;

final class CryptocurrencyJsonRepositoryImpl extends AbstractJsonRepository<Cryptocurrency, String>
    implements CryptocurrencyRepository {

    CryptocurrencyJsonRepositoryImpl(Gson gson) {
        super(
            gson,
            JsonPathFactory.CRYPTOCURRENCY_FILE.getPath(),
            TypeToken.getParameterized(Set.class, Cryptocurrency.class).getType(),
            Cryptocurrency::getSymbol // Вказуємо, що ідентифікатором є символ (String)
        );
    }

    @Override
    public Optional<Cryptocurrency> findBySymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Символ криптовалюти не може бути порожнім.");
        }

        return entities.stream()
            .filter(c -> c.getSymbol() != null && c.getSymbol().equalsIgnoreCase(symbol))
            .findFirst();
    }

    @Override
    public Optional<Cryptocurrency> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва криптовалюти не може бути порожньою.");
        }

        return entities.stream()
            .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(name))
            .findFirst();
    }

    @Override
    public void update(Cryptocurrency cryptocurrency) {
        if (cryptocurrency == null || !cryptocurrency.isValid()) {
            throw new IllegalArgumentException("Криптовалюта містить невалідні дані: " +
                (cryptocurrency != null ? cryptocurrency.getErrors() : "null"));
        }

        // Пошук існуючого запису
        Optional<Cryptocurrency> existing = findBySymbol(cryptocurrency.getSymbol());

        if (existing.isPresent()) {
            // Оновлення існуючого запису
            entities.remove(existing.get());
            System.out.printf("Криптовалюта %s була оновлена.%n", cryptocurrency.getSymbol());
        } else {
            System.out.printf("Криптовалюта %s не знайдена. Буде додана як нова.%n",
                cryptocurrency.getSymbol());
        }

        // Додавання або оновлення запису
        entities.add(cryptocurrency);
        saveChanges();
        System.out.printf("Криптовалюта %s успішно збережена.%n", cryptocurrency.getSymbol());
    }

    @Override
    public void delete(String symbol) {
        entities.removeIf(t -> t.getSymbol().equals(symbol));
        saveChanges();
    }
}
