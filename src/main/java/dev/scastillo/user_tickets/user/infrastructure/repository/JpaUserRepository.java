package dev.scastillo.user_tickets.user.infrastructure.repository;

import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }
}
