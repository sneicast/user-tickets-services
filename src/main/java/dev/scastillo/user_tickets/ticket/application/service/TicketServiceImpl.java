package dev.scastillo.user_tickets.ticket.application.service;

import dev.scastillo.user_tickets.shared.exception.NotFoundException;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.domain.service.TicketService;
import dev.scastillo.user_tickets.ticket.infrastructure.repository.JpaTicketRepository;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final JpaTicketRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Ticket> getAllTickets(int page, int size, UUID userId, TicketStatus status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<Ticket> ticketsPage;

        if (userId != null && status != null) {
            ticketsPage = ticketRepository.findByUserIdAndStatus(userId, status, pageable);
        } else if (userId != null) {
            ticketsPage = ticketRepository.findByUserId(userId, pageable);
        } else if (status != null) {
            ticketsPage = ticketRepository.findByStatus(status, pageable);
        } else {
            ticketsPage = ticketRepository.findAll(pageable);
        }
        return ticketsPage;

    }

    @Override
    @Transactional
    @CachePut(cacheNames = "ticket", key = "#result.id")
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "ticket", key = "#id")
    public Ticket getTicketById(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new NotFoundException("TICKET_NOT_FOUND", "El Ticket con ID " + id + " no existe"));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "ticket", key = "#ticket.id")
    public void updateTicket(Ticket ticket) {
        Ticket existingTicket = getTicketById(ticket.getId());
        existingTicket.setUser(ticket.getUser());
        existingTicket.setDescription(ticket.getDescription());
        existingTicket.setStatus(ticket.getStatus());
        ticketRepository.save(existingTicket);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "ticket", key = "#id")
    public void deleteTicket(UUID id) {
        ticketRepository.deleteById(id);
    }
}
