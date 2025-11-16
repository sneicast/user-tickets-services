package dev.scastillo.user_tickets.ticket.application.service;

import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.domain.service.TicketService;
import dev.scastillo.user_tickets.ticket.infrastructure.repository.JpaTicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final JpaTicketRepository ticketRepository;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicketById(UUID id) {
        return ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }

    @Override
    public void updateTicket(Ticket ticket) {

    }

    @Override
    public void deleteTicket(UUID id) {
        ticketRepository.deleteById(id);
    }
}
