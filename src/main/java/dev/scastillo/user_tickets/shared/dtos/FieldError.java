package dev.scastillo.user_tickets.shared.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error de validación en un campo específico")
public class FieldError {
    @Schema(description = "Nombre del campo", example = "description")
    private String field;
    @Schema(description = "Mensaje de error", example = "La descripción es obligatoria")
    private String message;
    @Schema(description = "Valor rechazado", example = "null")
    private Object rejectedValue;
}