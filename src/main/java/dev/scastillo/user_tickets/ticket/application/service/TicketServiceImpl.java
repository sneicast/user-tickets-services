package dev.scastillo.user_tickets.ticket.application.service;

import dev.scastillo.user_tickets.shared.exception.BadRequestException;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.domain.service.TicketService;
import dev.scastillo.user_tickets.ticket.infrastructure.repository.JpaTicketRepository;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final JpaTicketRepository ticketRepository;

    @Override
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
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicketById(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new BadRequestException("TICKET_NOT_FOUND", "El Ticket con ID " + id + " no existe"));
    }

    @Override
    public void updateTicket(Ticket ticket) {

    }

    @Override
    public void deleteTicket(UUID id) {
        ticketRepository.deleteById(id);
    }
}
