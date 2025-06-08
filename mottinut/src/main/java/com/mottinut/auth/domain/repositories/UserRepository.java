package com.mottinut.auth.domain.repositories;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UserId userId);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
    void deleteById(UserId userId);
}
