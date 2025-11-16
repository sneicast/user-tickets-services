package dev.scastillo.user_tickets.ticket.adapter.web.mapper;

import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketDto;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketFormDto;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    Ticket toDomain(TicketFormDto dto);
    TicketDto toDto(Ticket ticket);
}
