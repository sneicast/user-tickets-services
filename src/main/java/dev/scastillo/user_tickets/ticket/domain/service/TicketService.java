package dev.scastillo.user_tickets.ticket.domain.service;

import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;

import java.util.List;
import java.util.UUID;

public interface TicketService {

    List<Ticket> getAllTickets(int page, int size, UUID userId, TicketStatus status);
    Ticket createTicket(Ticket ticket);
    Ticket getTicketById(UUID id);
    void updateTicket(Ticket ticket);
    void deleteTicket(UUID id);
}
