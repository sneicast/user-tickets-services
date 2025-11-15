package dev.scastillo.user_tickets.user.adapter.web.controller;

import dev.scastillo.user_tickets.user.adapter.web.dto.UserCreateDto;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserDto;
import dev.scastillo.user_tickets.user.adapter.web.mapper.UserMapper;
import dev.scastillo.user_tickets.user.domain.services.UserServices;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@AllArgsConstructor
public class UserController {
    private final UserServices userServices;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@RequestBody UserCreateDto userCreateDto) {
        var user = userMapper.toDomain(userCreateDto);
        var createdUser = userServices.createUser(user);
        return userMapper.toDto(createdUser);
    }
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") String id) {
        var user = userServices.getUserById(java.util.UUID.fromString(id));
        return userMapper.toDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        var users = userServices.getAllUsers();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }
    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") String id, @RequestBody UserCreateDto userUpdateDto) {
        var user = userMapper.toDomain(userUpdateDto);
        user.setId(java.util.UUID.fromString(id));
        userServices.updateUser(user);
    }
}
