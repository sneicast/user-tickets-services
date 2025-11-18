package dev.scastillo.user_tickets.unit.ticket.infrastructure.repository;

import dev.scastillo.user_tickets.shared.exception.InternalServerException;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.infrastructure.repository.JpaTicketRepository;
import dev.scastillo.user_tickets.ticket.infrastructure.repository.SpringDataTicketRepository;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
public class JpaTicketRepositoryTest {
    @Mock
    private SpringDataTicketRepository repository;

    @InjectMocks
    private JpaTicketRepository jpaTicketRepository;

    @Test
    void findById_whenTicketExists_shouldReturnOptionalWithTicket() {
        UUID ticketId = UUID.randomUUID();
        Ticket expectedTicket = new Ticket();
        expectedTicket.setId(ticketId);

        when(repository.findById(eq(ticketId))).thenReturn(Optional.of(expectedTicket));

        Optional<Ticket> result = jpaTicketRepository.findById(ticketId);

        assertTrue(result.isPresent());
        assertSame(expectedTicket, result.get());
        verify(repository, times(1)).findById(eq(ticketId));
    }

    @Test
    void findById_whenTicketNotExists_shouldReturnEmptyOptional() {
        UUID ticketId = UUID.randomUUID();

        when(repository.findById(eq(ticketId))).thenReturn(Optional.empty());

        Optional<Ticket> result = jpaTicketRepository.findById(ticketId);

        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(eq(ticketId));
    }

    @Test
    void findById_whenExceptionOccurs_shouldThrowInternalServerException() {
        UUID ticketId = UUID.randomUUID();

        when(repository.findById(eq(ticketId))).thenThrow(new RuntimeException("Database error"));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaTicketRepository.findById(ticketId);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).findById(eq(ticketId));
    }

    @Test
    void save_whenSuccessful_shouldReturnSavedTicket() {
        Ticket ticketToSave = new Ticket();
        ticketToSave.setDescription("Test ticket");

        Ticket savedTicket = new Ticket();
        savedTicket.setId(UUID.randomUUID());
        savedTicket.setDescription("Test ticket");

        when(repository.save(eq(ticketToSave))).thenReturn(savedTicket);

        Ticket result = jpaTicketRepository.save(ticketToSave);

        assertSame(savedTicket, result);
        verify(repository, times(1)).save(eq(ticketToSave));
    }

    @Test
    void save_whenExceptionOccurs_shouldThrowInternalServerException() {
        Ticket ticket = new Ticket();
        ticket.setDescription("Test ticket");

        when(repository.save(any(Ticket.class))).thenThrow(new RuntimeException("Database error"));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaTicketRepository.save(ticket);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).save(eq(ticket));
    }

    @Test
    void deleteById_whenSuccessful_shouldCallRepositoryDeleteById() {
        UUID ticketId = UUID.randomUUID();

        doNothing().when(repository).deleteById(eq(ticketId));

        jpaTicketRepository.deleteById(ticketId);

        verify(repository, times(1)).deleteById(eq(ticketId));
    }

    @Test
    void deleteById_whenExceptionOccurs_shouldThrowInternalServerException() {
        UUID ticketId = UUID.randomUUID();

        doThrow(new RuntimeException("Database error")).when(repository).deleteById(eq(ticketId));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaTicketRepository.deleteById(ticketId);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).deleteById(eq(ticketId));
    }

    @Test
    void findAll_whenSuccessful_shouldReturnPageOfTickets() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> expectedPage = new PageImpl<>(List.of());

        when(repository.findAll(eq(pageable))).thenReturn(expectedPage);

        Page<Ticket> result = jpaTicketRepository.findAll(pageable);

        assertSame(expectedPage, result);
        verify(repository, times(1)).findAll(eq(pageable));
    }

    @Test
    void findAll_whenExceptionOccurs_shouldThrowInternalServerException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Database error"));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaTicketRepository.findAll(pageable);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).findAll(eq(pageable));
    }

    @Test
    void findByUserId_whenSuccessful_shouldReturnPageOfTickets() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> expectedPage = new PageImpl<>(List.of());

        when(repository.findByUserId(eq(userId), eq(pageable))).thenReturn(expectedPage);

        Page<Ticket> result = jpaTicketRepository.findByUserId(userId, pageable);

        assertSame(expectedPage, result);
        verify(repository, times(1)).findByUserId(eq(userId), eq(pageable));
    }

    @Test
    void findByUserId_whenExceptionOccurs_shouldThrowInternalServerException() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findByUserId(any(UUID.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaTicketRepository.findByUserId(userId, pageable);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).findByUserId(eq(userId), eq(pageable));
    }

    @Test
    void findByStatus_whenSuccessful_shouldReturnPageOfTickets() {
        TicketStatus status = TicketStatus.values()[0];
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> expectedPage = new PageImpl<>(List.of());

        when(repository.findByStatus(eq(status), eq(pageable))).thenReturn(expectedPage);

        Page<Ticket> result = jpaTicketRepository.findByStatus(status, pageable);

        assertSame(expectedPage, result);
        verify(repository, times(1)).findByStatus(eq(status), eq(pageable));
    }

    @Test
    void findByStatus_whenExceptionOccurs_shouldThrowInternalServerException() {
        TicketStatus status = TicketStatus.values()[0];
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findByStatus(any(TicketStatus.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaTicketRepository.findByStatus(status, pageable);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).findByStatus(eq(status), eq(pageable));
    }

    @Test
    void findByUserIdAndStatus_whenSuccessful_shouldReturnPageOfTickets() {
        UUID userId = UUID.randomUUID();
        TicketStatus status = TicketStatus.values()[0];
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ticket> expectedPage = new PageImpl<>(List.of());

        when(repository.findByUserIdAndStatus(eq(userId), eq(status), eq(pageable)))
                .thenReturn(expectedPage);

        Page<Ticket> result = jpaTicketRepository.findByUserIdAndStatus(userId, status, pageable);

        assertSame(expectedPage, result);
        verify(repository, times(1)).findByUserIdAndStatus(eq(userId), eq(status), eq(pageable));
    }

    @Test
    void findByUserIdAndStatus_whenExceptionOccurs_shouldThrowInternalServerException() {
        UUID userId = UUID.randomUUID();
        TicketStatus status = TicketStatus.values()[0];
        Pageable pageable = PageRequest.of(0, 10);

        when(repository.findByUserIdAndStatus(any(UUID.class), any(TicketStatus.class), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        InternalServerException exception = assertThrows(InternalServerException.class, () -> {
            jpaTicketRepository.findByUserIdAndStatus(userId, status, pageable);
        });

        assertEquals("DATABASE_ERROR", exception.getCode());
        assertEquals("Error al acceder a la base de datos", exception.getMessage());
        verify(repository, times(1)).findByUserIdAndStatus(eq(userId), eq(status), eq(pageable));
    }


}
