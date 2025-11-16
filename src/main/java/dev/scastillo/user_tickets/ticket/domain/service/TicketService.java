package dev.scastillo.user_tickets.ticket.domain.service;

import dev.scastillo.user_tickets.shared.dtos.PagedResponse;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    Page<Ticket> getAllTickets(int page, int size, UUID userId, TicketStatus status);
    Ticket createTicket(Ticket ticket);
    Ticket getTicketById(UUID id);
    void updateTicket(Ticket ticket);
    void deleteTicket(UUID id);
}
