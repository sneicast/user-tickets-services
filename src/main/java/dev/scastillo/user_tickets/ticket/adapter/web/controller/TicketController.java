package dev.scastillo.user_tickets.ticket.adapter.web.controller;

import dev.scastillo.user_tickets.shared.dtos.PagedResponse;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketDto;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketFormDto;
import dev.scastillo.user_tickets.ticket.adapter.web.mapper.TicketMapper;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.domain.service.TicketService;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.services.UserServices;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/tickets")
@AllArgsConstructor
public class TicketController {
    private  final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final UserServices userServices;

    @PostMapping
    public TicketDto createTicket(@RequestBody @Valid TicketFormDto ticketFormDto) {
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
    public PagedResponse<TicketDto> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") UUID userId,
            @RequestParam(defaultValue = "") TicketStatus status

    ) {
        Page<Ticket> tickets = ticketService.getAllTickets(page, size, userId, status);
        PagedResponse<TicketDto> pagedResponse = new PagedResponse<>();
        pagedResponse.setPage(tickets.getNumber());
        pagedResponse.setSize(tickets.getSize());
        pagedResponse.setTotalItems(tickets.getTotalElements());
        pagedResponse.setTotalPages(tickets.getTotalPages());
        pagedResponse.setData(tickets.getContent().stream()
                .map(ticketMapper::toDto)
                .toList());
        return pagedResponse;

    }

    @PutMapping("/{id}")
    public void updateTicket(@PathVariable("id") UUID id, @RequestBody @Valid TicketFormDto ticketFormDto) {
        var ticket = ticketMapper.toDomain(ticketFormDto);
        ticket.setId(id);
        ticketService.updateTicket(ticket);
    }
    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable("id") UUID id) {
        ticketService.deleteTicket(id);
    }

}
