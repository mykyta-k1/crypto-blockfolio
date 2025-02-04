package com.blockfolio.crypto.persistence.entity;

import com.blockfolio.crypto.persistence.Entity;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class User extends Entity<UUID> {

    private final String password;
    private final LocalDateTime createdAt;
    private String username;
    private String email;
    private Set<UUID> portfolios;

    public User(UUID id, String username, String email, String password) {
        super(id);
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.portfolios = new LinkedHashSet<>();
    }


}
