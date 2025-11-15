package dev.scastillo.user_tickets.user.domain.repository;

import dev.scastillo.user_tickets.user.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    List<User> findAll();
    User save(User user);

}
