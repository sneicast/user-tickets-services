package dev.scastillo.user_tickets.user.adapter.web.controller;

import dev.scastillo.user_tickets.shared.dtos.PagedResponse;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserDto;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserFormDto;
import dev.scastillo.user_tickets.user.adapter.web.mapper.UserMapper;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.services.UserServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/users")
@AllArgsConstructor
public class UserController {
    private final UserServices userServices;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@RequestBody @Valid UserFormDto userFormDto) {
        var user = userMapper.toDomain(userFormDto);
        var createdUser = userServices.createUser(user);
        return userMapper.toDto(createdUser);
    }
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") UUID id) {
        var user = userServices.getUserById(id);
        return userMapper.toDto(user);
    }

    @GetMapping
    public PagedResponse<UserDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<User> users = userServices.getAllUsers(page, size);
        return PagedResponse.<UserDto>builder()
                .page(users.getNumber())
                .size(users.getSize())
                .totalItems(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .data(users.map(userMapper::toDto).toList())
                .build();

    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") UUID id, @RequestBody @Valid UserFormDto userFormDto) {
        var user = userMapper.toDomain(userFormDto);
        user.setId(id);
        userServices.updateUser(user);
    }
}
