package dev.scastillo.user_tickets.ticket.adapter.web.controller;

import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketDto;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketFormDto;
import dev.scastillo.user_tickets.ticket.adapter.web.mapper.TicketMapper;
import dev.scastillo.user_tickets.ticket.domain.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/tickets")
@AllArgsConstructor
public class TicketController {
    private  final TicketService ticketService;
    private final TicketMapper ticketMapper;

    @PostMapping
    public TicketDto createTicket(@RequestBody TicketFormDto ticketFormDto) {
        var ticket = ticketMapper.toDomain(ticketFormDto);
        var createdTicket = ticketService.createTicket(ticket);
        return ticketMapper.toDto(createdTicket);
    }

    @GetMapping("/{id}")
    public TicketDto getTicketById(@PathVariable("id") UUID id) {
        var ticket = ticketService.getTicketById(id);
        return ticketMapper.toDto(ticket);
    }

    @GetMapping
    public List<TicketDto> getAllTickets() {
        var tickets = ticketService.getAllTickets();
        return tickets.stream()
                .map(ticketMapper::toDto)
                .toList();
    }

    @PutMapping("/{id}")
    public void updateTicket(@PathVariable("id") UUID id, @RequestBody TicketFormDto ticketFormDto) {
        var ticket = ticketMapper.toDomain(ticketFormDto);
        ticket.setId(id);
        ticketService.updateTicket(ticket);
    }
    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable("id") UUID id) {
        ticketService.deleteTicket(id);
    }

}
