package dev.scastillo.user_tickets.ticket.infrastructure.repository;

import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataTicketRepository extends JpaRepository<Ticket, UUID> {
}
