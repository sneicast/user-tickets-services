package dev.scastillo.user_tickets.user.adapter.web.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String firsName;
    private String lastName;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
