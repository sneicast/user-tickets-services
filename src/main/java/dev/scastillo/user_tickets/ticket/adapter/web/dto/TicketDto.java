package dev.scastillo.user_tickets.ticket.adapter.web.dto;

import dev.scastillo.user_tickets.user.adapter.web.dto.UserDto;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class TicketDto {
    private UUID id;
    private String description;
    private UserDto user;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private TicketStatus status;
}
