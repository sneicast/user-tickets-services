package dev.scastillo.user_tickets.user.adapter.web.dto;

import dev.scastillo.user_tickets.shared.dtos.PagedResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta paginada de usuarios")
public class PagedUserResponse extends PagedResponse<UserDto> {}
