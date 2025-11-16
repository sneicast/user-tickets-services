package dev.scastillo.user_tickets.ticket.adapter.web.dto;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class TicketFormDto {
    private UUID userId;
    private String description;
    private TicketStatus status;
}
