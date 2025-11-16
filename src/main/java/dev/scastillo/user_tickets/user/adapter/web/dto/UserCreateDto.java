package dev.scastillo.user_tickets.user.adapter.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserCreateDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String firsName;
    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;
}
