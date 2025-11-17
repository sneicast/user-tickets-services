package dev.scastillo.user_tickets.ticket.adapter.web.dto;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class TicketFormDto {
    @NotNull(message = "El ID del usuario es obligatorio")
    @Schema(description = "Usuario que creó el ticket")
    private UUID userId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Schema(description = "Descripción del ticket", example = "Error en el sistema de pagos", required = true)
    private String description;

    @NotNull(message = "El estado es obligatorio y debe ser ABIERTO o CERRADO")
    @Schema(description = "Estado actual del ticket", example = "ABIERTO", allowableValues = {"ABIERTO", "CERRADO"})
    private TicketStatus status;
}
