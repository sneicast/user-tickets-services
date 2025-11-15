package dev.scastillo.user_tickets.user.infrastructure.repository;

import dev.scastillo.user_tickets.user.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataUserRepository extends JpaRepository<User, UUID> {
}
