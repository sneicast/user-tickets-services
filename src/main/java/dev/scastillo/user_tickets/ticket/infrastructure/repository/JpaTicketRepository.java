package dev.scastillo.user_tickets.ticket.infrastructure.repository;

import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.domain.repository.TicketRepository;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Ticket> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<Ticket> findByUserId(UUID userId, Pageable pageable) {
        return repository.findByUserId( userId,  pageable);
    }

    @Override
    public Page<Ticket> findByStatus(TicketStatus status, Pageable pageable) {
        return repository.findByStatus( status,  pageable);
    }

    @Override
    public Page<Ticket> findByUserIdAndStatus(UUID userId, TicketStatus status, Pageable pageable) {
        return repository.findByUserIdAndStatus( userId,  status,  pageable);
    }


}
