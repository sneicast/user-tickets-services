package dev.scastillo.user_tickets.unit.ticket.application.service;

import dev.scastillo.user_tickets.shared.exception.NotFoundException;
import dev.scastillo.user_tickets.ticket.application.service.TicketServiceImpl;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.infrastructure.repository.JpaTicketRepository;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {
    @Mock
    private JpaTicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void getAllTickets_whenUserIdAndStatus_callsFindByUserIdAndStatus_withPagedAndSorted() {
        int page = 1, size = 5;
        UUID userId = UUID.randomUUID();
        TicketStatus status = TicketStatus.values()[0];
        Page<Ticket> expected = new PageImpl<>(List.of());

        when(ticketRepository.findByUserIdAndStatus(eq(userId), eq(status), any(Pageable.class)))
                .thenReturn(expected);

        Page<Ticket> result = ticketService.getAllTickets(page, size, userId, status);
        assertSame(expected, result);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(ticketRepository, times(1)).findByUserIdAndStatus(eq(userId), eq(status), captor.capture());
        verify(ticketRepository, never()).findByUserId(any(), any());
        verify(ticketRepository, never()).findByStatus(any(), any());
        verify(ticketRepository, never()).findAll(any(Pageable.class));

        assertPageable(captor.getValue(), page, size);
    }

    @Test
    void getAllTickets_whenOnlyUserId_callsFindByUserId_withPagedAndSorted() {
        int page = 0, size = 10;
        UUID userId = UUID.randomUUID();
        Page<Ticket> expected = new PageImpl<>(List.of());

        when(ticketRepository.findByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(expected);

        Page<Ticket> result = ticketService.getAllTickets(page, size, userId, null);
        assertSame(expected, result);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(ticketRepository, times(1)).findByUserId(eq(userId), captor.capture());
        verify(ticketRepository, never()).findByUserIdAndStatus(any(), any(), any());
        verify(ticketRepository, never()).findByStatus(any(), any());
        verify(ticketRepository, never()).findAll(any(Pageable.class));

        assertPageable(captor.getValue(), page, size);
    }

    @Test
    void getAllTickets_whenOnlyStatus_callsFindByStatus_withPagedAndSorted() {
        int page = 2, size = 20;
        TicketStatus status = TicketStatus.values()[0];
        Page<Ticket> expected = new PageImpl<>(List.of());

        when(ticketRepository.findByStatus(eq(status), any(Pageable.class)))
                .thenReturn(expected);

        Page<Ticket> result = ticketService.getAllTickets(page, size, null, status);
        assertSame(expected, result);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(ticketRepository, times(1)).findByStatus(eq(status), captor.capture());
        verify(ticketRepository, never()).findByUserIdAndStatus(any(), any(), any());
        verify(ticketRepository, never()).findByUserId(any(), any());
        verify(ticketRepository, never()).findAll(any(Pageable.class));

        assertPageable(captor.getValue(), page, size);
    }

    @Test
    void getAllTickets_whenNoFilters_callsFindAll_withPagedAndSorted() {
        int page = 3, size = 15;
        Page<Ticket> expected = new PageImpl<>(List.of());

        when(ticketRepository.findAll(any(Pageable.class)))
                .thenReturn(expected);

        Page<Ticket> result = ticketService.getAllTickets(page, size, null, null);
        assertSame(expected, result);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(ticketRepository, times(1)).findAll(captor.capture());
        verify(ticketRepository, never()).findByUserIdAndStatus(any(), any(), any());
        verify(ticketRepository, never()).findByUserId(any(), any());
        verify(ticketRepository, never()).findByStatus(any(), any());

        assertPageable(captor.getValue(), page, size);
    }

    private void assertPageable(Pageable pageable, int expectedPage, int expectedSize) {
        assertEquals(expectedPage, pageable.getPageNumber());
        assertEquals(expectedSize, pageable.getPageSize());
        Sort.Order order = pageable.getSort().getOrderFor("createAt");
        assertNotNull(order, "Debe ordenar por 'createAt'");
        assertEquals(Sort.Direction.DESC, order.getDirection(), "El orden debe ser descendente");
    }

    @Test
    void createTicket_shouldSaveAndReturnTicket() {
        Ticket inputTicket = new Ticket();
        inputTicket.setDescription("Test ticket");

        Ticket savedTicket = new Ticket();
        savedTicket.setId(UUID.randomUUID());
        savedTicket.setDescription("Test ticket");

        when(ticketRepository.save(eq(inputTicket))).thenReturn(savedTicket);

        Ticket result = ticketService.createTicket(inputTicket);

        assertSame(savedTicket, result);
        verify(ticketRepository, times(1)).save(eq(inputTicket));
    }

    @Test
    void createTicket_shouldPassTicketToRepository() {
        Ticket ticket = new Ticket();
        ticket.setDescription("Another test");

        Ticket expected = new Ticket();
        expected.setId(UUID.randomUUID());

        when(ticketRepository.save(any(Ticket.class))).thenReturn(expected);

        Ticket result = ticketService.createTicket(ticket);

        assertNotNull(result);
        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository, times(1)).save(captor.capture());
        assertSame(ticket, captor.getValue());
    }

    @Test
    void getTicketById_whenTicketExists_shouldReturnTicket() {
        UUID ticketId = UUID.randomUUID();
        Ticket expectedTicket = new Ticket();
        expectedTicket.setId(ticketId);
        expectedTicket.setDescription("Test ticket");

        when(ticketRepository.findById(eq(ticketId))).thenReturn(java.util.Optional.of(expectedTicket));

        Ticket result = ticketService.getTicketById(ticketId);

        assertSame(expectedTicket, result);
        verify(ticketRepository, times(1)).findById(eq(ticketId));
    }

    @Test
    void getTicketById_whenTicketNotExists_shouldThrowNotFoundException() {
        UUID ticketId = UUID.randomUUID();

        when(ticketRepository.findById(eq(ticketId))).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            ticketService.getTicketById(ticketId);
        });

        assertEquals("TICKET_NOT_FOUND", exception.getCode());
        assertTrue(exception.getMessage().contains(ticketId.toString()));
        verify(ticketRepository, times(1)).findById(eq(ticketId));
    }

    @Test
    void updateTicket_shouldUpdateExistingTicketFields() {
        UUID ticketId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Ticket existingTicket = new Ticket();
        existingTicket.setId(ticketId);
        existingTicket.setDescription("Old description");

        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(ticketId);
        updatedTicket.setDescription("New description");
        updatedTicket.setStatus(TicketStatus.values()[0]);

        when(ticketRepository.findById(eq(ticketId))).thenReturn(java.util.Optional.of(existingTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(existingTicket);

        ticketService.updateTicket(updatedTicket);

        ArgumentCaptor<Ticket> captor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository, times(1)).findById(eq(ticketId));
        verify(ticketRepository, times(1)).save(captor.capture());

        Ticket savedTicket = captor.getValue();
        assertEquals("New description", savedTicket.getDescription());
        assertEquals(updatedTicket.getStatus(), savedTicket.getStatus());
    }

    @Test
    void updateTicket_whenTicketNotExists_shouldThrowNotFoundException() {
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);

        when(ticketRepository.findById(eq(ticketId))).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            ticketService.updateTicket(ticket);
        });

        verify(ticketRepository, times(1)).findById(eq(ticketId));
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void deleteTicket_shouldCallRepositoryDeleteById() {
        UUID ticketId = UUID.randomUUID();
        Ticket mockTicket = new Ticket();
        mockTicket.setId(ticketId);

        when(ticketRepository.findById(eq(ticketId))).thenReturn(Optional.of(mockTicket));
        doNothing().when(ticketRepository).deleteById(eq(ticketId));

        ticketService.deleteTicket(ticketId);

        verify(ticketRepository, times(1)).findById(eq(ticketId));
        verify(ticketRepository, times(1)).deleteById(eq(ticketId));
    }
}
