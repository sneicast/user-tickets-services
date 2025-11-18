package dev.scastillo.user_tickets.unit.user.adapter.web.controller;

import dev.scastillo.user_tickets.shared.dtos.PagedResponse;
import dev.scastillo.user_tickets.user.adapter.web.controller.UserController;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserDto;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserFormDto;
import dev.scastillo.user_tickets.user.adapter.web.mapper.UserMapper;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.services.UserServices;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserServices userServices;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_whenValidUserFormDto_shouldReturnCreatedUser() {
        UserFormDto userFormDto = new UserFormDto();
        userFormDto.setFirsName("John");
        userFormDto.setLastName("Doe");

        User user = new User();
        user.setFirsName("John");
        user.setLastName("Doe");

        User createdUser = new User();
        createdUser.setId(UUID.randomUUID());
        createdUser.setFirsName("John");
        createdUser.setLastName("Doe");

        UserDto userDto = new UserDto();
        userDto.setId(createdUser.getId());
        userDto.setFirsName("John");
        userDto.setLastName("Doe");

        when(userMapper.toDomain(eq(userFormDto))).thenReturn(user);
        when(userServices.createUser(eq(user))).thenReturn(createdUser);
        when(userMapper.toDto(eq(createdUser))).thenReturn(userDto);

        ResponseEntity<UserDto> response = userController.createUser(userFormDto);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdUser.getId(), response.getBody().getId());
        assertEquals("John", response.getBody().getFirsName());
        assertEquals("Doe", response.getBody().getLastName());

        verify(userMapper, times(1)).toDomain(eq(userFormDto));
        verify(userServices, times(1)).createUser(eq(user));
        verify(userMapper, times(1)).toDto(eq(createdUser));
    }

    @Test
    void createUser_whenServiceThrowsException_shouldPropagateException() {
        UserFormDto userFormDto = new UserFormDto();
        userFormDto.setFirsName("Test");
        userFormDto.setLastName("User");

        User user = new User();

        when(userMapper.toDomain(eq(userFormDto))).thenReturn(user);
        when(userServices.createUser(eq(user)))
                .thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () -> {
            userController.createUser(userFormDto);
        });

        verify(userMapper, times(1)).toDomain(eq(userFormDto));
        verify(userServices, times(1)).createUser(eq(user));
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void createUser_shouldCallMapperAndServiceInCorrectOrder() {
        UserFormDto userFormDto = new UserFormDto();
        User user = new User();
        User createdUser = new User();
        createdUser.setId(UUID.randomUUID());
        UserDto userDto = new UserDto();

        when(userMapper.toDomain(any())).thenReturn(user);
        when(userServices.createUser(any())).thenReturn(createdUser);
        when(userMapper.toDto(any())).thenReturn(userDto);

        userController.createUser(userFormDto);

        var inOrder = inOrder(userMapper, userServices);
        inOrder.verify(userMapper).toDomain(eq(userFormDto));
        inOrder.verify(userServices).createUser(eq(user));
        inOrder.verify(userMapper).toDto(eq(createdUser));
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUserDto() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setFirsName("Jane");
        user.setLastName("Doe");

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setFirsName("Jane");
        userDto.setLastName("Doe");

        when(userServices.getUserById(eq(userId))).thenReturn(user);
        when(userMapper.toDto(eq(user))).thenReturn(userDto);

        UserDto result = userController.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Jane", result.getFirsName());
        assertEquals("Doe", result.getLastName());

        verify(userServices, times(1)).getUserById(eq(userId));
        verify(userMapper, times(1)).toDto(eq(user));
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldThrowNotFoundException() {
        UUID userId = UUID.randomUUID();

        when(userServices.getUserById(eq(userId)))
                .thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> {
            userController.getUserById(userId);
        });

        verify(userServices, times(1)).getUserById(eq(userId));
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void getUserById_shouldCallServiceAndMapperInCorrectOrder() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        UserDto userDto = new UserDto();

        when(userServices.getUserById(any())).thenReturn(user);
        when(userMapper.toDto(any())).thenReturn(userDto);

        userController.getUserById(userId);

        var inOrder = inOrder(userServices, userMapper);
        inOrder.verify(userServices).getUserById(eq(userId));
        inOrder.verify(userMapper).toDto(eq(user));
    }

    @Test
    void getAllUsers_whenUsersExist_shouldReturnPagedResponse() {
        int page = 0;
        int size = 10;

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setFirsName("John");
        user1.setLastName("Doe");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setFirsName("Jane");
        user2.setLastName("Smith");

        Page<User> userPage = new PageImpl<>(
                List.of(user1, user2),
                org.springframework.data.domain.PageRequest.of(page, size),
                2
        );

        UserDto userDto1 = new UserDto();
        userDto1.setId(user1.getId());
        userDto1.setFirsName("John");
        userDto1.setLastName("Doe");

        UserDto userDto2 = new UserDto();
        userDto2.setId(user2.getId());
        userDto2.setFirsName("Jane");
        userDto2.setLastName("Smith");

        when(userServices.getAllUsers(eq(page), eq(size))).thenReturn(userPage);
        when(userMapper.toDto(eq(user1))).thenReturn(userDto1);
        when(userMapper.toDto(eq(user2))).thenReturn(userDto2);

        ResponseEntity<PagedResponse<UserDto>> response = userController.getAllUsers(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getPage());
        assertEquals(10, response.getBody().getSize());
        assertEquals(2, response.getBody().getTotalItems());
        assertEquals(1, response.getBody().getTotalPages());
        assertEquals(2, response.getBody().getData().size());
        assertEquals("John", response.getBody().getData().get(0).getFirsName());
        assertEquals("Jane", response.getBody().getData().get(1).getFirsName());

        verify(userServices, times(1)).getAllUsers(eq(page), eq(size));
        verify(userMapper, times(2)).toDto(any(User.class));
    }

    @Test
    void getAllUsers_whenNoUsersExist_shouldReturnEmptyPagedResponse() {
        int page = 0;
        int size = 10;

        Page<User> emptyPage = new PageImpl<>(
                List.of(),
                org.springframework.data.domain.PageRequest.of(page, size),
                0
        );

        when(userServices.getAllUsers(eq(page), eq(size))).thenReturn(emptyPage);

        ResponseEntity<PagedResponse<UserDto>> response = userController.getAllUsers(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getPage());
        assertEquals(10, response.getBody().getSize());
        assertEquals(0, response.getBody().getTotalItems());
        assertEquals(0, response.getBody().getTotalPages());
        assertTrue(response.getBody().getData().isEmpty());

        verify(userServices, times(1)).getAllUsers(eq(page), eq(size));
        verify(userMapper, never()).toDto(any(User.class));
    }

    @Test
    void getAllUsers_withCustomPageAndSize_shouldReturnCorrectPagedResponse() {
        int page = 2;
        int size = 5;

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirsName("Test");

        Page<User> userPage = new PageImpl<>(
                List.of(user),
                org.springframework.data.domain.PageRequest.of(page, size),
                11
        );

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirsName("Test");

        when(userServices.getAllUsers(eq(page), eq(size))).thenReturn(userPage);
        when(userMapper.toDto(eq(user))).thenReturn(userDto);

        ResponseEntity<PagedResponse<UserDto>> response = userController.getAllUsers(page, size);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getPage());
        assertEquals(5, response.getBody().getSize());
        assertEquals(11, response.getBody().getTotalItems());
        assertEquals(3, response.getBody().getTotalPages());
        assertEquals(1, response.getBody().getData().size());

        verify(userServices, times(1)).getAllUsers(eq(page), eq(size));
        verify(userMapper, times(1)).toDto(eq(user));
    }

    @Test
    void getAllUsers_shouldCallServiceAndMapperInCorrectOrder() {
        int page = 0;
        int size = 10;

        User user = new User();
        Page<User> userPage = new PageImpl<>(List.of(user));
        UserDto userDto = new UserDto();

        when(userServices.getAllUsers(anyInt(), anyInt())).thenReturn(userPage);
        when(userMapper.toDto(any())).thenReturn(userDto);

        userController.getAllUsers(page, size);

        var inOrder = inOrder(userServices, userMapper);
        inOrder.verify(userServices).getAllUsers(eq(page), eq(size));
        inOrder.verify(userMapper).toDto(eq(user));
    }
    @Test
    void updateUser_whenValidUserFormDto_shouldUpdateAndReturnNoContent() {
        UUID userId = UUID.randomUUID();

        UserFormDto userFormDto = new UserFormDto();
        userFormDto.setFirsName("Updated Name");
        userFormDto.setLastName("Updated LastName");

        User user = new User();
        user.setFirsName("Updated Name");
        user.setLastName("Updated LastName");

        when(userMapper.toDomain(eq(userFormDto))).thenReturn(user);
        doNothing().when(userServices).updateUser(any(User.class));

        ResponseEntity<Void> response = userController.updateUser(userId, userFormDto);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(userMapper, times(1)).toDomain(eq(userFormDto));
        verify(userServices, times(1)).updateUser(argThat(u ->
                u.getId().equals(userId) &&
                        u.getFirsName().equals("Updated Name") &&
                        u.getLastName().equals("Updated LastName")
        ));
    }

    @Test
    void updateUser_shouldSetUserIdCorrectly() {
        UUID userId = UUID.randomUUID();
        UserFormDto userFormDto = new UserFormDto();
        User user = new User();

        when(userMapper.toDomain(eq(userFormDto))).thenReturn(user);
        doNothing().when(userServices).updateUser(any(User.class));

        userController.updateUser(userId, userFormDto);

        verify(userServices).updateUser(argThat(u -> u.getId().equals(userId)));
    }

    @Test
    void updateUser_whenServiceThrowsException_shouldPropagateException() {
        UUID userId = UUID.randomUUID();
        UserFormDto userFormDto = new UserFormDto();
        userFormDto.setFirsName("Test");
        userFormDto.setLastName("User");

        User user = new User();

        when(userMapper.toDomain(eq(userFormDto))).thenReturn(user);
        doThrow(new RuntimeException("Update failed")).when(userServices).updateUser(any(User.class));

        assertThrows(RuntimeException.class, () -> {
            userController.updateUser(userId, userFormDto);
        });

        verify(userMapper, times(1)).toDomain(eq(userFormDto));
        verify(userServices, times(1)).updateUser(any(User.class));
    }

    @Test
    void updateUser_shouldCallMapperAndServiceInCorrectOrder() {
        UUID userId = UUID.randomUUID();
        UserFormDto userFormDto = new UserFormDto();
        User user = new User();

        when(userMapper.toDomain(any())).thenReturn(user);
        doNothing().when(userServices).updateUser(any(User.class));

        userController.updateUser(userId, userFormDto);

        var inOrder = inOrder(userMapper, userServices);
        inOrder.verify(userMapper).toDomain(eq(userFormDto));
        inOrder.verify(userServices).updateUser(eq(user));
    }

    @Test
    void updateUser_whenUserNotFound_shouldThrowException() {
        UUID userId = UUID.randomUUID();
        UserFormDto userFormDto = new UserFormDto();
        User user = new User();

        when(userMapper.toDomain(eq(userFormDto))).thenReturn(user);
        doThrow(new RuntimeException("User not found")).when(userServices).updateUser(any(User.class));

        assertThrows(RuntimeException.class, () -> {
            userController.updateUser(userId, userFormDto);
        });

        verify(userServices, times(1)).updateUser(any(User.class));
    }


}
