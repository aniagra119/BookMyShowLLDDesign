package com.bookmyshow.model;

import com.bookmyshow.enums.UserRole;
import java.util.Objects;

public class User {
    private final String userId;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final UserRole role;

    public User(String userId, String name, String email, String phoneNumber) {
        this(userId, name, email, phoneNumber, UserRole.CUSTOMER);
    }

    public User(String userId, String name, String email, String phoneNumber, UserRole role) {
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.email = Objects.requireNonNull(email, "email cannot be null");
        this.phoneNumber = Objects.requireNonNull(phoneNumber, "phoneNumber cannot be null");
        this.role = Objects.requireNonNull(role, "role cannot be null");
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

    public UserRole getRole() {
        return role;
    }

    public boolean hasRole(UserRole targetRole) {
        return this.role == targetRole;
    }
}
