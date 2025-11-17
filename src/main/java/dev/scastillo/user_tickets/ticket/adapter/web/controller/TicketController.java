package dev.scastillo.user_tickets.ticket.adapter.web.controller;

import dev.scastillo.user_tickets.shared.dtos.ErrorResponse;
import dev.scastillo.user_tickets.shared.dtos.PagedResponse;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.PagedTicketResponse;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketDto;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketFormDto;
import dev.scastillo.user_tickets.ticket.adapter.web.mapper.TicketMapper;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.domain.service.TicketService;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.services.UserServices;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/tickets")
@Tag(name = "Tickets", description = "API para gestión de tickets")
@AllArgsConstructor
public class TicketController {
    private  final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final UserServices userServices;

    @Operation(
            summary = "Crear nuevo ticket",
            description = "Crea un nuevo ticket en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ticket creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TicketDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
    })
    @PostMapping
    public ResponseEntity<TicketDto> createTicket(@RequestBody @Valid TicketFormDto ticketFormDto) {
        var ticket = ticketMapper.toDomain(ticketFormDto);
        User user = userServices.getUserById(ticketFormDto.getUserId());
        ticket.setUser(user);
        var createdTicket = ticketService.createTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketMapper.toDto(createdTicket));
    }

    @Operation(
            summary = "Obtener ticket por ID",
            description = "Retorna un ticket específico basado en su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TicketDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public TicketDto getTicketById(@PathVariable("id") UUID id) {
        var ticket = ticketService.getTicketById(id);
        return ticketMapper.toDto(ticket);
    }

    @Operation(
            summary = "Obtener lista de tickets",
            description = "Retorna una lista paginada de tickets con filtros opcionales por userId y status"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de tickets obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedTicketResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
    })
    @GetMapping
    public PagedResponse<TicketDto> getAllTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") UUID userId,
            @RequestParam(defaultValue = "") TicketStatus status

    ) {
        Page<Ticket> tickets = ticketService.getAllTickets(page, size, userId, status);
        return PagedResponse.<TicketDto>builder()
                .page(tickets.getNumber())
                .size(tickets.getSize())
                .totalItems(tickets.getTotalElements())
                .totalPages(tickets.getTotalPages())
                .data(tickets.getContent().stream()
                        .map(ticketMapper::toDto)
                        .toList())
                .build();
    }

    @Operation(
            summary = "Actualizar ticket",
            description = "Actualiza un ticket existente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Ticket actualizado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void>  updateTicket(@PathVariable("id") UUID id, @RequestBody @Valid TicketFormDto ticketFormDto) {
        var ticket = ticketMapper.toDomain(ticketFormDto);
        ticket.setId(id);
        ticket.setUser(userServices.getUserById(ticketFormDto.getUserId()));
        ticketService.updateTicket(ticket);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Eliminar ticket",
            description = "Elimina un ticket del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ticket eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable("id") UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

}
