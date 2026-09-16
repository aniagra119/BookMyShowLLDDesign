package com.bookmyshow.model;

import java.util.Objects;

public class User {
    private final String userId;
    private final String name;
    private final String email;
    private final String phoneNumber;

    public User(String userId, String name, String email, String phoneNumber) {
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber cannot be null");
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
