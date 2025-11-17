package dev.scastillo.user_tickets.ticket.infrastructure.repository;

import dev.scastillo.user_tickets.shared.exception.InternalServerException;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.domain.repository.TicketRepository;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@AllArgsConstructor
public class JpaTicketRepository implements TicketRepository {

    private final SpringDataTicketRepository repository;

    @Override
    public Optional<Ticket> findById(UUID id) {
        try {
            return repository.findById(id);
        } catch (Exception ex) {
            log.error("Eror al realizar consulta de ticket en la db por ID: {}", id, ex);
            throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }
    }

    @Override
    public Ticket save(Ticket ticket) {
        try {
            return repository.save(ticket);
        } catch (Exception ex) {
            log.error("Error al guardar ticket en la db: {}", ticket, ex);
            throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            log.error("Error al eliminar ticket en la db por ID: {}", id, ex);
            throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }
    }

    @Override
    public Page<Ticket> findAll(Pageable pageable) {
        try {
            return repository.findAll(pageable);
        } catch (Exception ex) {
            log.error("Error al realizar consulta de tickets en la db", ex);
            throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }
    }

    @Override
    public Page<Ticket> findByUserId(UUID userId, Pageable pageable) {
        try {
            return repository.findByUserId(userId, pageable);
        } catch (Exception ex) {
            log.error("Error al realizar consulta de tickets en la db por userId: {}", userId, ex);
            throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }
    }

    @Override
    public Page<Ticket> findByStatus(TicketStatus status, Pageable pageable) {
        try {
            return repository.findByStatus(status, pageable);
        } catch (Exception ex) {
            log.error("Error al realizar consulta de tickets en la db por status: {}", status, ex);
            throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }
    }

    @Override
    public Page<Ticket> findByUserIdAndStatus(UUID userId, TicketStatus status, Pageable pageable) {
        try {
            return repository.findByUserIdAndStatus(userId, status, pageable);
        } catch (Exception ex) {
            log.error("Error al realizar consulta de tickets en la db por userId: {} y status: {}", userId, status, ex);
            throw new InternalServerException("DATABASE_ERROR", "Error al acceder a la base de datos");
        }
    }


}
