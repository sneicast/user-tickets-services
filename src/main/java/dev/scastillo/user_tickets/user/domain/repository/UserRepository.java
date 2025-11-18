package dev.scastillo.user_tickets.user.domain.repository;

import dev.scastillo.user_tickets.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID id);
    Page<User> findAll(Pageable pageable);
    User save(User user);
    void deleteAll();

}
