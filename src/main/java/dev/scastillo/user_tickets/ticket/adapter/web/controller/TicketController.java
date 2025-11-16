package dev.scastillo.user_tickets.ticket.adapter.web.controller;

import dev.scastillo.user_tickets.shared.code_error.ErrorCode;
import dev.scastillo.user_tickets.shared.exception.BadRequestException;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketDto;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketFormDto;
import dev.scastillo.user_tickets.ticket.adapter.web.mapper.TicketMapper;
import dev.scastillo.user_tickets.ticket.domain.service.TicketService;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.services.UserServices;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static dev.scastillo.user_tickets.shared.code_error.ErrorCode.ROLE_NOT_FOUND;

@RestController
@RequestMapping("api/tickets")
@AllArgsConstructor
public class TicketController {
    private  final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final UserServices userServices;

    @PostMapping
    public TicketDto createTicket(@RequestBody TicketFormDto ticketFormDto) {
        var ticket = ticketMapper.toDomain(ticketFormDto);
        User user = userServices.getUserById(ticketFormDto.getUserId());
        ticket.setUser(user);
        var createdTicket = ticketService.createTicket(ticket);
        //throw new BadRequestException(ErrorCode.ROLE_NOT_FOUND.getCode(), "Simulated error for demonstration purposes");
        return ticketMapper.toDto(createdTicket);
    }

    @GetMapping("/{id}")
    public TicketDto getTicketById(@PathVariable("id") UUID id) {
        var ticket = ticketService.getTicketById(id);
        return ticketMapper.toDto(ticket);
    }

    @GetMapping
    public List<TicketDto> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") UUID userId,
            @RequestParam(defaultValue = "") TicketStatus status

    ) {
        var tickets = ticketService.getAllTickets(page, size, userId, status);

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
