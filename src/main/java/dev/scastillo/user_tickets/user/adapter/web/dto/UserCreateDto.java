package dev.scastillo.user_tickets.user.adapter.web.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserCreateDto {
    private String firsName;
    private String lastName;
}
