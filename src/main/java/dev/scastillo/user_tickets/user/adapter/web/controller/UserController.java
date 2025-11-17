package dev.scastillo.user_tickets.user.adapter.web.controller;

import dev.scastillo.user_tickets.shared.dtos.ErrorResponse;
import dev.scastillo.user_tickets.shared.dtos.PagedResponse;
import dev.scastillo.user_tickets.user.adapter.web.dto.PagedUserResponse;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserDto;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserFormDto;
import dev.scastillo.user_tickets.user.adapter.web.mapper.UserMapper;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/users")
@AllArgsConstructor
@Tag(name = "Users", description = "API para gestión de usuarios")
public class UserController {
    private final UserServices userServices;
    private final UserMapper userMapper;

    @Operation(
            summary = "Crear nuevo usuario",
            description = "Crea un nuevo usuario en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<UserDto>  createUser(@RequestBody @Valid UserFormDto userFormDto) {
        var user = userMapper.toDomain(userFormDto);
        var createdUser = userServices.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(createdUser));
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Obtiene los detalles de un usuario específico mediante su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") UUID id) {
        var user = userServices.getUserById(id);
        return userMapper.toDto(user);
    }

    @Operation(
            summary = "Obtener todos los usuarios con paginación",
            description = "Retorna una lista paginada de todos los usuarios en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagedUserResponse.class)
                    )
            )
    })

    @GetMapping
    public ResponseEntity<PagedResponse<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<User> users = userServices.getAllUsers(page, size);
        PagedResponse<UserDto> body = PagedResponse.<UserDto>builder()
                .page(users.getNumber())
                .size(users.getSize())
                .totalItems(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .data(users.map(userMapper::toDto).toList())
                .build();
        return ResponseEntity.ok(body);

    }

    @Operation(
            summary = "Actualizar usuario existente",
            description = "Actualiza la información de un usuario existente mediante su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario actualizado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable("id") UUID id, @RequestBody @Valid UserFormDto userFormDto) {
        var user = userMapper.toDomain(userFormDto);
        user.setId(id);
        userServices.updateUser(user);
        return ResponseEntity.noContent().build();
    }
}
