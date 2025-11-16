package dev.scastillo.user_tickets.shared.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldError {
    private String field;
    private String message;
    private Object rejectedValue;
}