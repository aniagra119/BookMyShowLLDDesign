package com.bookmyshow.repository;

import com.bookmyshow.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String userId);
    Optional<User> findByEmail(String email);
    List<User> findAll();
}
