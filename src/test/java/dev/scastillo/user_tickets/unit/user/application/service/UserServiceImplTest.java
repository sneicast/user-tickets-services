package dev.scastillo.user_tickets.unit.user.application.service;

import dev.scastillo.user_tickets.shared.exception.NotFoundException;
import dev.scastillo.user_tickets.user.application.service.UserServiceImpl;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsers_whenUsersExist_shouldReturnPagedUsers() {
        int page = 0;
        int size = 10;

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setFirsName("User 1");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setFirsName("User 2");

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<User> userPage = new PageImpl<>(List.of(user1, user2), expectedPageable, 2);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userService.getAllUsers(page, size);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("User 1", result.getContent().get(0).getFirsName());
        assertEquals("User 2", result.getContent().get(1).getFirsName());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllUsers_whenNoUsersExist_shouldReturnEmptyPage() {
        int page = 0;
        int size = 10;

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<User> emptyPage = new PageImpl<>(List.of(), expectedPageable, 0);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<User> result = userService.getAllUsers(page, size);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllUsers_shouldCreatePageableWithCorrectSorting() {
        int page = 1;
        int size = 20;

        Page<User> userPage = new PageImpl<>(List.of());

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        userService.getAllUsers(page, size);

        verify(userRepository).findAll(argThat(pageable ->
                pageable.getPageNumber() == page &&
                        pageable.getPageSize() == size &&
                        pageable.getSort().getOrderFor("createAt") != null &&
                        pageable.getSort().getOrderFor("createAt").getDirection() == Sort.Direction.DESC
        ));
    }

    @Test
    void getAllUsers_withCustomPageAndSize_shouldReturnCorrectPage() {
        int page = 2;
        int size = 5;

        User user = new User();
        user.setId(UUID.randomUUID());

        Pageable expectedPageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        Page<User> userPage = new PageImpl<>(List.of(user), expectedPageable, 11);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        Page<User> result = userService.getAllUsers(page, size);

        assertNotNull(result);
        assertEquals(2, result.getNumber());
        assertEquals(5, result.getSize());
        assertEquals(11, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void createUser_whenValidUser_shouldSaveAndReturnUser() {
        User user = new User();
        user.setFirsName("John");
        user.setLastName("Doe");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setFirsName("John");
        savedUser.setLastName("Doe");

        when(userRepository.save(eq(user))).thenReturn(savedUser);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("John", result.getFirsName());
        assertEquals("Doe", result.getLastName());
        verify(userRepository, times(1)).save(eq(user));
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setFirsName("Jane");
        user.setLastName("Smith");

        when(userRepository.findById(eq(userId))).thenReturn(java.util.Optional.of(user));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Jane", result.getFirsName());
        assertEquals("Smith", result.getLastName());
        verify(userRepository, times(1)).findById(eq(userId));
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldThrowNotFoundException() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(eq(userId))).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("USER_NOT_FOUND", exception.getCode());
        assertEquals("El usuario con ID " + userId + " no existe", exception.getMessage());
        verify(userRepository, times(1)).findById(eq(userId));
    }

    @Test
    void updateUser_whenUserExists_shouldUpdateUser() {
        UUID userId = UUID.randomUUID();

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirsName("Old Name");
        existingUser.setLastName("Old LastName");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirsName("New Name");
        updatedUser.setLastName("New LastName");

        when(userRepository.findById(eq(userId))).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        userService.updateUser(updatedUser);

        assertEquals("New Name", existingUser.getFirsName());
        assertEquals("New LastName", existingUser.getLastName());
        verify(userRepository, times(1)).findById(eq(userId));
        verify(userRepository, times(1)).save(eq(existingUser));
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldThrowNotFoundException() {
        UUID userId = UUID.randomUUID();

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirsName("New Name");
        updatedUser.setLastName("New LastName");

        when(userRepository.findById(eq(userId))).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.updateUser(updatedUser);
        });

        assertEquals("USER_NOT_FOUND", exception.getCode());
        verify(userRepository, times(1)).findById(eq(userId));
        verify(userRepository, never()).save(any(User.class));
    }
}
