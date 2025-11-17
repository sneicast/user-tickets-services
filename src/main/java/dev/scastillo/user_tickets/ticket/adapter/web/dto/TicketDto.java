package dev.scastillo.user_tickets.ticket.adapter.web.dto;

import dev.scastillo.user_tickets.user.adapter.web.dto.UserDto;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Schema(description = "Entidad que representa un ticket en el sistema")
public class TicketDto {
    @Schema(description = "Identificador único del ticket", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    @Schema(description = "Descripción del ticket", example = "Error en el sistema de pagos", required = true)
    private String description;
    @Schema(description = "Usuario que creó el ticket")
    private UserDto user;
    @Schema(description = "Fecha y hora de creación del ticket", example = "2025-11-16T10:30:45")
    private LocalDateTime createAt;
    @Schema(description = "Fecha y hora de última actualización", example = "2025-11-16T15:20:30")
    private LocalDateTime updateAt;
    @Schema(description = "Estado actual del ticket", example = "ABIERTO", allowableValues = {"ABIERTO", "CERRADO"})
    private TicketStatus status;
}
