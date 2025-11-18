package dev.scastillo.user_tickets.unit.ticket.adapter.web.controller;

import dev.scastillo.user_tickets.shared.dtos.PagedResponse;
import dev.scastillo.user_tickets.ticket.adapter.web.controller.TicketController;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketDto;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketFormDto;
import dev.scastillo.user_tickets.ticket.adapter.web.mapper.TicketMapper;
import dev.scastillo.user_tickets.ticket.domain.model.Ticket;
import dev.scastillo.user_tickets.ticket.domain.service.TicketService;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.services.UserServices;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TicketControllerTest {
    @Mock
    private TicketService ticketService;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private UserServices userServices;

    @InjectMocks
    private TicketController ticketController;

    @Test
    void createTicket_whenValidInput_shouldReturnCreatedTicket() {
        UUID userId = UUID.randomUUID();
        TicketFormDto formDto = new TicketFormDto();
        formDto.setUserId(userId);
        formDto.setDescription("Test ticket");

        Ticket mappedTicket = new Ticket();
        mappedTicket.setDescription("Test ticket");

        User user = new User();
        user.setId(userId);

        Ticket createdTicket = new Ticket();
        createdTicket.setId(UUID.randomUUID());
        createdTicket.setDescription("Test ticket");
        createdTicket.setUser(user);

        TicketDto expectedDto = new TicketDto();
        expectedDto.setDescription("Test ticket");

        when(ticketMapper.toDomain(eq(formDto))).thenReturn(mappedTicket);
        when(userServices.getUserById(eq(userId))).thenReturn(user);
        when(ticketService.createTicket(any(Ticket.class))).thenReturn(createdTicket);
        when(ticketMapper.toDto(eq(createdTicket))).thenReturn(expectedDto);

        ResponseEntity<TicketDto> response = ticketController.createTicket(formDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertSame(expectedDto, response.getBody());
        verify(ticketMapper, times(1)).toDomain(eq(formDto));
        verify(userServices, times(1)).getUserById(eq(userId));
        verify(ticketService, times(1)).createTicket(any(Ticket.class));
        verify(ticketMapper, times(1)).toDto(eq(createdTicket));
    }

    @Test
    void createTicket_shouldSetUserOnTicket() {
        UUID userId = UUID.randomUUID();
        TicketFormDto formDto = new TicketFormDto();
        formDto.setUserId(userId);

        Ticket mappedTicket = mock(Ticket.class);
        User user = new User();
        user.setId(userId);

        Ticket createdTicket = new Ticket();
        TicketDto ticketDto = new TicketDto();

        when(ticketMapper.toDomain(any(TicketFormDto.class))).thenReturn(mappedTicket);
        when(userServices.getUserById(eq(userId))).thenReturn(user);
        when(ticketService.createTicket(any(Ticket.class))).thenReturn(createdTicket);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDto);

        ticketController.createTicket(formDto);

        verify(mappedTicket, times(1)).setUser(eq(user));
    }

    @Test
    void getTicketById_whenTicketExists_shouldReturnTicketDto() {
        UUID ticketId = UUID.randomUUID();

        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        ticket.setDescription("Test ticket");

        TicketDto expectedDto = new TicketDto();
        expectedDto.setId(ticketId);
        expectedDto.setDescription("Test ticket");

        when(ticketService.getTicketById(eq(ticketId))).thenReturn(ticket);
        when(ticketMapper.toDto(eq(ticket))).thenReturn(expectedDto);

        TicketDto result = ticketController.getTicketById(ticketId);

        assertNotNull(result);
        assertSame(expectedDto, result);
        verify(ticketService, times(1)).getTicketById(eq(ticketId));
        verify(ticketMapper, times(1)).toDto(eq(ticket));
    }

    @Test
    void getTicketById_whenServiceThrowsException_shouldPropagateException() {
        UUID ticketId = UUID.randomUUID();

        when(ticketService.getTicketById(eq(ticketId)))
                .thenThrow(new RuntimeException("Ticket not found"));

        assertThrows(RuntimeException.class, () -> {
            ticketController.getTicketById(ticketId);
        });

        verify(ticketService, times(1)).getTicketById(eq(ticketId));
        verify(ticketMapper, never()).toDto(any(Ticket.class));
    }

    @Test
    void getAllTickets_whenCalledWithDefaultParameters_shouldReturnPagedResponse() {
        int page = 0;
        int size = 10;
        UUID userId = null;
        TicketStatus status = null;

        Ticket ticket1 = new Ticket();
        ticket1.setId(UUID.randomUUID());
        Ticket ticket2 = new Ticket();
        ticket2.setId(UUID.randomUUID());

        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket1, ticket2), PageRequest.of(page, size), 2);

        TicketDto ticketDto1 = new TicketDto();
        ticketDto1.setId(ticket1.getId());
        TicketDto ticketDto2 = new TicketDto();
        ticketDto2.setId(ticket2.getId());

        when(ticketService.getAllTickets(eq(page), eq(size), eq(userId), eq(status)))
                .thenReturn(ticketPage);
        when(ticketMapper.toDto(eq(ticket1))).thenReturn(ticketDto1);
        when(ticketMapper.toDto(eq(ticket2))).thenReturn(ticketDto2);

        PagedResponse<TicketDto> result = ticketController.getAllTickets(page, size, userId, status);

        assertNotNull(result);
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(2, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getData().size());
        verify(ticketService, times(1)).getAllTickets(eq(page), eq(size), eq(userId), eq(status));
        verify(ticketMapper, times(2)).toDto(any(Ticket.class));
    }

    @Test
    void getAllTickets_whenCalledWithUserIdFilter_shouldReturnFilteredPagedResponse() {
        int page = 0;
        int size = 10;
        UUID userId = UUID.randomUUID();
        TicketStatus status = null;

        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());

        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket), PageRequest.of(page, size), 1);

        TicketDto ticketDto = new TicketDto();
        ticketDto.setId(ticket.getId());

        when(ticketService.getAllTickets(eq(page), eq(size), eq(userId), eq(status)))
                .thenReturn(ticketPage);
        when(ticketMapper.toDto(eq(ticket))).thenReturn(ticketDto);

        PagedResponse<TicketDto> result = ticketController.getAllTickets(page, size, userId, status);

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        verify(ticketService, times(1)).getAllTickets(eq(page), eq(size), eq(userId), eq(status));
    }

    @Test
    void getAllTickets_whenCalledWithStatusFilter_shouldReturnFilteredPagedResponse() {
        int page = 0;
        int size = 10;
        UUID userId = null;
        TicketStatus status = TicketStatus.ABIERTO;

        Page<Ticket> ticketPage = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);

        when(ticketService.getAllTickets(eq(page), eq(size), eq(userId), eq(status)))
                .thenReturn(ticketPage);

        PagedResponse<TicketDto> result = ticketController.getAllTickets(page, size, userId, status);

        assertNotNull(result);
        assertEquals(0, result.getData().size());
        assertEquals(0, result.getTotalItems());
        verify(ticketService, times(1)).getAllTickets(eq(page), eq(size), eq(userId), eq(status));
    }

    @Test
    void getAllTickets_whenCalledWithBothFilters_shouldReturnFilteredPagedResponse() {
        int page = 1;
        int size = 5;
        UUID userId = UUID.randomUUID();
        TicketStatus status = TicketStatus.CERRADO;

        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());

        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket), PageRequest.of(page, size), 1);

        TicketDto ticketDto = new TicketDto();

        when(ticketService.getAllTickets(eq(page), eq(size), eq(userId), eq(status)))
                .thenReturn(ticketPage);
        when(ticketMapper.toDto(any(Ticket.class))).thenReturn(ticketDto);

        PagedResponse<TicketDto> result = ticketController.getAllTickets(page, size, userId, status);

        assertNotNull(result);
        assertEquals(1, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(1, result.getData().size());
        verify(ticketService, times(1)).getAllTickets(eq(page), eq(size), eq(userId), eq(status));
    }

    @Test
    void updateTicket_whenValidInput_shouldUpdateAndReturnNoContent() {
        UUID ticketId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TicketFormDto formDto = new TicketFormDto();
        formDto.setUserId(userId);
        formDto.setDescription("Updated description");

        Ticket mappedTicket = mock(Ticket.class);
        User user = new User();
        user.setId(userId);

        when(ticketMapper.toDomain(eq(formDto))).thenReturn(mappedTicket);
        when(userServices.getUserById(eq(userId))).thenReturn(user);

        ResponseEntity<Void> response = ticketController.updateTicket(ticketId, formDto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(ticketMapper, times(1)).toDomain(eq(formDto));
        verify(mappedTicket, times(1)).setId(eq(ticketId));
        verify(userServices, times(1)).getUserById(eq(userId));
        verify(mappedTicket, times(1)).setUser(eq(user));
        verify(ticketService, times(1)).updateTicket(eq(mappedTicket));
    }

    @Test
    void updateTicket_shouldSetIdAndUserBeforeUpdate() {
        UUID ticketId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TicketFormDto formDto = new TicketFormDto();
        formDto.setUserId(userId);

        Ticket mappedTicket = mock(Ticket.class);
        User user = new User();
        user.setId(userId);

        when(ticketMapper.toDomain(any(TicketFormDto.class))).thenReturn(mappedTicket);
        when(userServices.getUserById(eq(userId))).thenReturn(user);

        ticketController.updateTicket(ticketId, formDto);

        verify(mappedTicket, times(1)).setId(eq(ticketId));
        verify(mappedTicket, times(1)).setUser(eq(user));
    }

    @Test
    void updateTicket_whenUserNotFound_shouldPropagateException() {
        UUID ticketId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TicketFormDto formDto = new TicketFormDto();
        formDto.setUserId(userId);

        Ticket mappedTicket = mock(Ticket.class);

        when(ticketMapper.toDomain(eq(formDto))).thenReturn(mappedTicket);
        when(userServices.getUserById(eq(userId)))
                .thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () -> {
            ticketController.updateTicket(ticketId, formDto);
        });

        verify(ticketMapper, times(1)).toDomain(eq(formDto));
        verify(mappedTicket, times(1)).setId(eq(ticketId));
        verify(userServices, times(1)).getUserById(eq(userId));
        verify(ticketService, never()).updateTicket(any(Ticket.class));
    }

    @Test
    void updateTicket_whenServiceThrowsException_shouldPropagateException() {
        UUID ticketId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TicketFormDto formDto = new TicketFormDto();
        formDto.setUserId(userId);

        Ticket mappedTicket = mock(Ticket.class);
        User user = new User();

        when(ticketMapper.toDomain(eq(formDto))).thenReturn(mappedTicket);
        when(userServices.getUserById(eq(userId))).thenReturn(user);
        doThrow(new RuntimeException("Update failed"))
                .when(ticketService).updateTicket(any(Ticket.class));

        assertThrows(RuntimeException.class, () -> {
            ticketController.updateTicket(ticketId, formDto);
        });

        verify(ticketService, times(1)).updateTicket(eq(mappedTicket));
    }

    @Test
    void deleteTicket_whenValidId_shouldDeleteAndReturnNoContent() {
        UUID ticketId = UUID.randomUUID();

        doNothing().when(ticketService).deleteTicket(eq(ticketId));

        ResponseEntity<Void> response = ticketController.deleteTicket(ticketId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(ticketService, times(1)).deleteTicket(eq(ticketId));
    }

    @Test
    void deleteTicket_whenTicketNotFound_shouldPropagateException() {
        UUID ticketId = UUID.randomUUID();

        doThrow(new RuntimeException("Ticket not found"))
                .when(ticketService).deleteTicket(eq(ticketId));

        assertThrows(RuntimeException.class, () -> {
            ticketController.deleteTicket(ticketId);
        });

        verify(ticketService, times(1)).deleteTicket(eq(ticketId));
    }

    @Test
    void deleteTicket_whenServiceThrowsException_shouldPropagateException() {
        UUID ticketId = UUID.randomUUID();

        doThrow(new RuntimeException("Delete operation failed"))
                .when(ticketService).deleteTicket(eq(ticketId));

        assertThrows(RuntimeException.class, () -> {
            ticketController.deleteTicket(ticketId);
        });

        verify(ticketService, times(1)).deleteTicket(eq(ticketId));
    }

}
