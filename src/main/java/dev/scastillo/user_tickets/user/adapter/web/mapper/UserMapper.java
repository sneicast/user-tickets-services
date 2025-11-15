package dev.scastillo.user_tickets.user.adapter.web.mapper;

import dev.scastillo.user_tickets.user.adapter.web.dto.UserCreateDto;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserDto;
import dev.scastillo.user_tickets.user.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserCreateDto dto);
    UserDto toDto(User user);
}
