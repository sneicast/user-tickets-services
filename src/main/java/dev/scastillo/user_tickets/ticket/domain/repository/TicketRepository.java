package dev.scastillo.user_tickets.ticket.domain.repository;

import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {
    Optional<Ticket> findById(UUID id);
    Ticket save(Ticket ticket);
    void deleteById(UUID id);
    Page<Ticket> findAll(Pageable pageable);

    Page<Ticket> findByUserId(UUID userId, Pageable pageable);
    Page<Ticket> findByStatus (TicketStatus status, Pageable pageable);
    Page<Ticket> findByUserIdAndStatus (UUID userId, TicketStatus status, Pageable pageable);
}
