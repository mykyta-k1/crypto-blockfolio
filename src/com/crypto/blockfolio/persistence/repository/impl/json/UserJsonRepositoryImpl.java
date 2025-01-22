package com.crypto.blockfolio.persistence.repository.impl.json;

import com.crypto.blockfolio.persistence.entity.User;
import com.crypto.blockfolio.persistence.repository.contracts.UserRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Optional;
import java.util.Set;

final class UserJsonRepositoryImpl extends AbstractJsonRepository<User>
    implements UserRepository {

    UserJsonRepositoryImpl(Gson gson) {
        super(gson, JsonPathFactory.USERS_FILE.getPath(), TypeToken
            .getParameterized(Set.class, User.class)
            .getType());
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return entities.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }
    /*
    @Override
    public Optional<User> findByEmail(String email) {
        return entities.stream().filter(u -> u.getUsername().equals(email)).findFirst();
    }
     */

    @Override
    public void update(User user) {
        Optional<User> existingUser = entities.stream()
            .filter(u -> u.getId().equals(user.getId()))
            .findFirst();

        if (existingUser.isPresent()) {
            entities.remove(existingUser.get());
            entities.add(user);
            saveChanges();
            System.out.println("Користувач з ID " + user.getId() + " успішно оновлений.");
        } else {
            throw new RuntimeException("Користувач з ID " + user.getId() + " не знайдений.");
        }
    }


    @Override
    public Optional<User> findByEmail(String email) {
        System.out.println("Пошук користувача за email: " + email);
        return entities.stream()
            .peek(user -> System.out.println("Перевіряємо користувача: " + user.getEmail()))
            .filter(u -> u.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }

}
