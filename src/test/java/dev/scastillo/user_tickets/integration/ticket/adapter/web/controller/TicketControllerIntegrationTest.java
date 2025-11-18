package dev.scastillo.user_tickets.integration.ticket.adapter.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scastillo.user_tickets.ticket.adapter.web.dto.TicketFormDto;
import dev.scastillo.user_tickets.ticket.domain.repository.TicketRepository;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserFormDto;
import dev.scastillo.user_tickets.user.domain.repository.UserRepository;
import dev.scastillo.user_tickets.utils.enums.TicketStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TicketControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createTicket_whenValidPayload_shouldReturn201AndPersist() throws Exception {
        String userId = createUser("John", "Doe");

        TicketFormDto ticketFormDto = new TicketFormDto();
        ticketFormDto.setUserId(UUID.fromString(userId));
        ticketFormDto.setDescription("Test ticket description");
        ticketFormDto.setStatus(TicketStatus.ABIERTO);

        MvcResult result = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketFormDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.description").value("Test ticket description"))
                .andExpect(jsonPath("$.status").value("ABIERTO"))
                .andReturn();

        JsonNode created = objectMapper.readTree(result.getResponse().getContentAsString());
        String ticketId = created.get("id").asText();

        var persisted = ticketRepository.findById(UUID.fromString(ticketId));
        assertTrue(persisted.isPresent());
        assertEquals("Test ticket description", persisted.get().getDescription());
        assertEquals(TicketStatus.ABIERTO, persisted.get().getStatus());
    }

    @Test
    void createTicket_whenMissingRequiredFields_shouldReturn400() throws Exception {
        TicketFormDto ticketFormDto = new TicketFormDto();

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketFormDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        Pageable pageable = PageRequest.of(0, 10);
        var tickets = ticketRepository.findAll(pageable);
        assertTrue(tickets.isEmpty());
    }

    @Test
    void createTicket_whenUserIdDoesNotExist_shouldReturn404() throws Exception {
        UUID nonExistentUserId = UUID.randomUUID();

        TicketFormDto ticketFormDto = new TicketFormDto();
        ticketFormDto.setUserId(nonExistentUserId);
        ticketFormDto.setDescription("Test ticket");
        ticketFormDto.setStatus(TicketStatus.ABIERTO);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketFormDto)))
                .andExpect(status().isNotFound());

        Pageable pageable = PageRequest.of(0, 10);
        var tickets = ticketRepository.findAll(pageable);
        assertTrue(tickets.isEmpty());
    }

    @Test
    void createTicket_whenInvalidJson_shouldReturn400() throws Exception {
        String invalidJson = "{\"userId\": \"abc\", \"description\": }";

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        Pageable pageable = PageRequest.of(0, 10);
        var tickets = ticketRepository.findAll(pageable);
        assertTrue(tickets.isEmpty());
    }

    @Test
    void createTicket_whenEmptyBody_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        Pageable pageable = PageRequest.of(0, 10);
        var tickets = ticketRepository.findAll(pageable);
        assertTrue(tickets.isEmpty());
    }

    @Test
    void createTicket_whenNullValues_shouldReturn400() throws Exception {
        TicketFormDto ticketFormDto = new TicketFormDto();
        ticketFormDto.setUserId(null);
        ticketFormDto.setDescription(null);
        ticketFormDto.setStatus(null);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketFormDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        Pageable pageable = PageRequest.of(0, 10);
        var tickets = ticketRepository.findAll(pageable);
        assertTrue(tickets.isEmpty());
    }

    @Test
    void getTicketById_whenTicketExists_shouldReturn200AndTicketData() throws Exception {
        String userId = createUser("Jane", "Smith");
        String ticketId = createTicket(userId, "Integration test ticket", TicketStatus.ABIERTO);

        mockMvc.perform(get("/api/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ticketId))
                .andExpect(jsonPath("$.description").value("Integration test ticket"))
                .andExpect(jsonPath("$.status").value("ABIERTO"));
    }

    @Test
    void getTicketById_whenTicketDoesNotExist_shouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/tickets/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTicketById_whenIdIsInvalid_shouldReturn400() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(get("/api/tickets/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTicketById_whenMultipleTicketsExist_shouldReturnCorrectTicket() throws Exception {
        String userId = createUser("Alice", "Wonder");
        String ticketId1 = createTicket(userId, "First ticket", TicketStatus.ABIERTO);
        String ticketId2 = createTicket(userId, "Second ticket", TicketStatus.CERRADO);

        mockMvc.perform(get("/api/tickets/{id}", ticketId2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId2))
                .andExpect(jsonPath("$.description").value("Second ticket"))
                .andExpect(jsonPath("$.status").value("CERRADO"));
    }

    private String createUser(String firstName, String lastName) throws Exception {
        UserFormDto dto = new UserFormDto();
        dto.setFirsName(firstName);
        dto.setLastName(lastName);

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(result.getResponse().getContentAsString());
        return created.get("id").asText();
    }

    private String createTicket(String userId, String description, TicketStatus status) throws Exception {
        TicketFormDto dto = new TicketFormDto();
        dto.setUserId(UUID.fromString(userId));
        dto.setDescription(description);
        dto.setStatus(status);

        MvcResult result = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = objectMapper.readTree(result.getResponse().getContentAsString());
        return created.get("id").asText();
    }

    @Test
    void getAllTickets_whenNoFilters_shouldReturnAllTickets() throws Exception {
        String userId1 = createUser("John", "Doe");
        String userId2 = createUser("Jane", "Smith");

        createTicket(userId1, "Ticket 1", TicketStatus.ABIERTO);
        createTicket(userId1, "Ticket 2", TicketStatus.CERRADO);
        createTicket(userId2, "Ticket 3", TicketStatus.ABIERTO);

        mockMvc.perform(get("/api/tickets")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    void getAllTickets_whenFilterByUserId_shouldReturnUserTickets() throws Exception {
        String userId1 = createUser("Alice", "Wonder");
        String userId2 = createUser("Bob", "Builder");

        createTicket(userId1, "Alice Ticket 1", TicketStatus.ABIERTO);
        createTicket(userId1, "Alice Ticket 2", TicketStatus.CERRADO);
        createTicket(userId2, "Bob Ticket", TicketStatus.ABIERTO);

        mockMvc.perform(get("/api/tickets")
                        .param("page", "0")
                        .param("size", "10")
                        .param("userId", userId1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getAllTickets_whenFilterByStatus_shouldReturnFilteredTickets() throws Exception {
        String userId = createUser("Test", "User");

        createTicket(userId, "Open Ticket 1", TicketStatus.ABIERTO);
        createTicket(userId, "Open Ticket 2", TicketStatus.ABIERTO);
        createTicket(userId, "Closed Ticket", TicketStatus.CERRADO);

        mockMvc.perform(get("/api/tickets")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "ABIERTO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getAllTickets_whenFilterByUserIdAndStatus_shouldReturnFilteredTickets() throws Exception {
        String userId1 = createUser("User", "One");
        String userId2 = createUser("User", "Two");

        createTicket(userId1, "User1 Open", TicketStatus.ABIERTO);
        createTicket(userId1, "User1 Closed", TicketStatus.CERRADO);
        createTicket(userId2, "User2 Open", TicketStatus.ABIERTO);

        mockMvc.perform(get("/api/tickets")
                        .param("page", "0")
                        .param("size", "10")
                        .param("userId", userId1)
                        .param("status", "ABIERTO")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.data[0].description").value("User1 Open"));
    }

    @Test
    void getAllTickets_whenPaginationApplied_shouldReturnCorrectPage() throws Exception {
        String userId = createUser("Paginated", "User");

        for (int i = 1; i <= 15; i++) {
            createTicket(userId, "Ticket " + i, TicketStatus.ABIERTO);
        }

        mockMvc.perform(get("/api/tickets")
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalItems").value(15))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.data.length()").value(5));
    }

    @Test
    void getAllTickets_whenNoTicketsExist_shouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/api/tickets")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void updateTicket_whenValidPayload_shouldReturn204AndUpdatePersisted() throws Exception {
        String userId = createUser("Original", "User");
        String ticketId = createTicket(userId, "Original description", TicketStatus.ABIERTO);

        TicketFormDto updateDto = new TicketFormDto();
        updateDto.setUserId(UUID.fromString(userId));
        updateDto.setDescription("Updated description");
        updateDto.setStatus(TicketStatus.CERRADO);

        mockMvc.perform(put("/api/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        var updated = ticketRepository.findById(UUID.fromString(ticketId));
        assertTrue(updated.isPresent());
        assertEquals("Updated description", updated.get().getDescription());
        assertEquals(TicketStatus.CERRADO, updated.get().getStatus());
    }

    @Test
    void updateTicket_whenTicketNotFound_shouldReturn404() throws Exception {
        String userId = createUser("Test", "User");
        UUID nonExistentId = UUID.randomUUID();

        TicketFormDto updateDto = new TicketFormDto();
        updateDto.setUserId(UUID.fromString(userId));
        updateDto.setDescription("Update attempt");
        updateDto.setStatus(TicketStatus.ABIERTO);

        mockMvc.perform(put("/api/tickets/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTicket_whenUserNotFound_shouldReturn404() throws Exception {
        String userId = createUser("Original", "User");
        String ticketId = createTicket(userId, "Test ticket", TicketStatus.ABIERTO);
        UUID nonExistentUserId = UUID.randomUUID();

        TicketFormDto updateDto = new TicketFormDto();
        updateDto.setUserId(nonExistentUserId);
        updateDto.setDescription("Update with invalid user");
        updateDto.setStatus(TicketStatus.ABIERTO);

        mockMvc.perform(put("/api/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTicket_whenMissingRequiredFields_shouldReturn400() throws Exception {
        String userId = createUser("Test", "User");
        String ticketId = createTicket(userId, "Test ticket", TicketStatus.ABIERTO);

        TicketFormDto updateDto = new TicketFormDto();

        mockMvc.perform(put("/api/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTicket_whenTicketExists_shouldReturn204AndRemovePersisted() throws Exception {
        String userId = createUser("Delete", "User");
        String ticketId = createTicket(userId, "To be deleted", TicketStatus.ABIERTO);

        mockMvc.perform(delete("/api/tickets/{id}", ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        var deleted = ticketRepository.findById(UUID.fromString(ticketId));
        assertFalse(deleted.isPresent());
    }

    @Test
    void deleteTicket_whenTicketNotFound_shouldReturn404() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/tickets/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTicket_whenInvalidId_shouldReturn400() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(delete("/api/tickets/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTicket_whenMultipleTicketsExist_shouldDeleteOnlySpecified() throws Exception {
        String userId = createUser("Multi", "Delete");
        String ticketId1 = createTicket(userId, "Keep this", TicketStatus.ABIERTO);
        String ticketId2 = createTicket(userId, "Delete this", TicketStatus.ABIERTO);

        mockMvc.perform(delete("/api/tickets/{id}", ticketId2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertTrue(ticketRepository.findById(UUID.fromString(ticketId1)).isPresent());
        assertFalse(ticketRepository.findById(UUID.fromString(ticketId2)).isPresent());
    }
}
