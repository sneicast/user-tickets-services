package dev.scastillo.user_tickets.unit.user.infrastructure.repository;

import dev.scastillo.user_tickets.shared.exception.InternalServerException;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.infrastructure.repository.JpaUserRepository;
import dev.scastillo.user_tickets.user.infrastructure.repository.SpringDataUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JpaUserRepositoryTest {
    @Mock
    private SpringDataUserRepository repository;

    @InjectMocks
    private JpaUserRepository jpaUserRepository;

    @Test
    void findById_whenUserExists_shouldReturnUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setFirsName("Test User");

        when(repository.findById(eq(userId))).thenReturn(Optional.of(user));

        Optional<User> result = jpaUserRepository.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals("Test User", result.get().getFirsName());
        verify(repository, times(1)).findById(eq(userId));
    }

    @Test
    void findById_whenUserDoesNotExist_shouldReturnEmpty() {
        UUID userId = UUID.randomUUID();

        when(repository.findById(eq(userId))).thenReturn(Optional.empty());

        Optional<User> result = jpaUserRepository.findById(userId);

        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(eq(userId));
    }

    @Test
    void findById_whenDataAccessExceptionOccurs_shouldThrowInternalServerException() {
        UUID userId = UUID.randomUUID();

        when(repository.findById(eq(userId)))
                .thenThrow(new DataAccessException("Database connection error") {});

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaUserRepository.findById(userId);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).findById(eq(userId));
    }

    @Test
    void findAll_whenUsersExist_shouldReturnPagedUsers() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);

        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setFirsName("User 1");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setFirsName("User 2");

        Page<User> userPage = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(repository.findAll(eq(pageable))).thenReturn(userPage);

        Page<User> result = jpaUserRepository.findAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("User 1", result.getContent().get(0).getFirsName());
        verify(repository, times(1)).findAll(eq(pageable));
    }

    @Test
    void findAll_whenNoUsersExist_shouldReturnEmptyPage() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(repository.findAll(eq(pageable))).thenReturn(emptyPage);

        Page<User> result = jpaUserRepository.findAll(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        verify(repository, times(1)).findAll(eq(pageable));
    }

    @Test
    void findAll_whenDataAccessExceptionOccurs_shouldThrowInternalServerException() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);

        when(repository.findAll(eq(pageable)))
                .thenThrow(new DataAccessException("Database connection error") {});

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaUserRepository.findAll(pageable);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).findAll(eq(pageable));
    }

    @Test
    void save_whenValidUser_shouldSaveAndReturnUser() {
        User user = new User();
        user.setFirsName("New User");
        user.setLastName("Test");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setFirsName("New User");
        savedUser.setLastName("Test");

        when(repository.save(eq(user))).thenReturn(savedUser);

        User result = jpaUserRepository.save(user);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("New User", result.getFirsName());
        assertEquals("Test", result.getLastName());
        verify(repository, times(1)).save(eq(user));
    }

    @Test
    void save_whenDataAccessExceptionOccurs_shouldThrowInternalServerException() {
        User user = new User();
        user.setFirsName("Test User");

        when(repository.save(eq(user)))
                .thenThrow(new DataAccessException("Database save error") {});

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaUserRepository.save(user);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).save(eq(user));
    }

}
