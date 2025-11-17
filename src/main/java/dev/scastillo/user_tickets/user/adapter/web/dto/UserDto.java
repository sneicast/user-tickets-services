package dev.scastillo.user_tickets.user.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserDto {
    @Schema(description = "Identificador único del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String firsName;
    @Schema(description = "Apellido del usuario", example = "Pérez", required = true)
    private String lastName;
    @Schema(description = "Fecha y hora de creación del usuario", example = "2025-11-16T10:30:45")
    private LocalDateTime createAt;
    @Schema(description = "Fecha y hora de última actualización", example = "2025-11-16T15:20:30")
    private LocalDateTime updateAt;
}
