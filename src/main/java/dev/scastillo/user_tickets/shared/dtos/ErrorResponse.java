package dev.scastillo.user_tickets.shared.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta estándar de error")
public class ErrorResponse {
    @Schema(description = "Timestamp del error", example = "2025-11-16T10:30:45.123")
    private LocalDateTime timestamp;
    @Schema(description = "Código de estado HTTP", example = "404")
    private int status;
    @Schema(description = "Nombre del error", example = "Not Found")
    private String error;
    @Schema(description = "Mensaje descriptivo del error", example = "Ticket no encontrado con id: 123e4567")
    private String message;
    @Schema(description = "Código de error personalizado", example = "ENTITY_NOT_FOUND")
    private String code;
    @Schema(description = "Lista de errores de validación de campos")
    private List<FieldError> fieldErrors;
}