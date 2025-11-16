package dev.scastillo.user_tickets.ticket.domain.repository;

import dev.scastillo.user_tickets.ticket.domain.model.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {
    Optional<Ticket> findById(UUID id);
    Ticket save(Ticket ticket);
    void deleteById(UUID id);
    List<Ticket> findAll();
}
