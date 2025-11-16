package dev.scastillo.user_tickets.ticket.infrastructure.repository;

import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.domain.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class JpaTicketRepository implements TicketRepository {

    private final SpringDataTicketRepository repository;

    @Override
    public Optional<Ticket> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Ticket save(Ticket ticket) {
        return repository.save(ticket);
    }

    @Override
    public void deleteById(UUID id) {
    repository.deleteById(id);
    }

    @Override
    public List<Ticket> findAll() {
        return repository.findAll();
    }
}
