package dev.scastillo.user_tickets.user.infrastructure.repository;

import dev.scastillo.user_tickets.shared.exception.InternalServerException;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@AllArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;

    @Override
    public Optional<User> findById(UUID id) {
        try {
            return repository.findById(id);
        } catch (DataAccessException ex) {
            log.error("Error al realizar consulta de user en la db por ID: {}", id, ex);
           throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }

    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        try {
            return repository.findAll(pageable);
        } catch (DataAccessException ex) {
            log.error("Error al realizar consulta de users en la db", ex);
            throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }
    }

    @Override
    public User save(User user) {
        try {
            return repository.save(user);
        } catch (DataAccessException ex) {
            log.error("Error al guardar usuario en la db: {}", user, ex);
            throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
