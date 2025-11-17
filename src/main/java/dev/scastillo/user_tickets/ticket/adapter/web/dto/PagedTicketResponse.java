package dev.scastillo.user_tickets.ticket.adapter.web.dto;

import dev.scastillo.user_tickets.shared.dtos.PagedResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta paginada de tickets")
public class PagedTicketResponse extends PagedResponse<TicketDto> {
}
