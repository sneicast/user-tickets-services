package dev.scastillo.user_tickets.user.adapter.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserFormDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    @Pattern(regexp = "^[a-záéíóúñüA-ZÁÉÍÓÚÑÜ\\s'-]+$", message = "El nombre solo debe contener letras, espacios, apóstrofos y guiones")
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String firsName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    @Pattern(regexp = "^[a-záéíóúñüA-ZÁÉÍÓÚÑÜ\\s'-]+$", message = "El apellido solo debe contener letras, espacios, apóstrofos y guiones")
    @Schema(description = "Apellido del usuario", example = "Pérez", required = true)
    private String lastName;
}
