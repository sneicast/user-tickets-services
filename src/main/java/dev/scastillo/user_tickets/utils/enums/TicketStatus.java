package dev.scastillo.user_tickets.utils.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estados posibles de un ticket")
public enum TicketStatus {
    @Schema(description = "Ticket recién Abierto")
    ABIERTO,
    @Schema(description = "Ticket resuelto y cerrado")
    CERRADO
}