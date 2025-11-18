package dev.scastillo.user_tickets.integration.user.adapter.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.scastillo.user_tickets.user.adapter.web.dto.UserFormDto;
import dev.scastillo.user_tickets.user.domain.model.User;
import dev.scastillo.user_tickets.user.domain.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_whenValidUserFormDto_shouldReturnCreatedUserWithStatus201() throws Exception {
        UserFormDto userFormDto = new UserFormDto();
        userFormDto.setFirsName("John");
        userFormDto.setLastName("Doe");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userFormDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.firsName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createAt"));
        var users = userRepository.findAll(pageable);
        assert users.getContent().size() == 1;
        assert users.getContent().get(0).getFirsName().equals("John");
        assert users.getContent().get(0).getLastName().equals("Doe");
    }

    @Test
    void createUser_whenMissingRequiredFields_shouldReturnBadRequest() throws Exception {
        UserFormDto userFormDto = new UserFormDto();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userFormDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createAt"));
        var users = userRepository.findAll(pageable);
        assert users.isEmpty();
    }

    @Test
    void createUser_whenInvalidJsonFormat_shouldReturnBadRequest() throws Exception {
        String invalidJson = "{\"firsName\": \"John\", \"lastName\": }";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createAt"));
        var users = userRepository.findAll(pageable);
        assert users.isEmpty();
    }

    @Test
    void createUser_whenEmptyRequestBody_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createAt"));
        var users = userRepository.findAll(pageable);
        assert users.isEmpty();
    }

    @Test
    void createUser_whenNullValues_shouldReturnBadRequest() throws Exception {
        UserFormDto userFormDto = new UserFormDto();
        userFormDto.setFirsName(null);
        userFormDto.setLastName(null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userFormDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createAt"));
        var users = userRepository.findAll(pageable);
        assert users.isEmpty();
    }

    @Test
    void getUserById_whenUserExists_shouldReturn200WithUserDto() throws Exception {
        UserFormDto userFormDto = new UserFormDto();
        userFormDto.setFirsName("John");
        userFormDto.setLastName("Doe");

        MvcResult createResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userFormDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String id = created.get("id").asText();

        mockMvc.perform(get("/api/users/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firsName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldReturn404() throws Exception {
        UUID notExistingId = UUID.randomUUID();

        mockMvc.perform(get("/api/users/{id}", notExistingId.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_whenUsersExist_shouldReturn200WithPagedResponse() throws Exception {
        createUser("John", "Doe");
        createUser("Jane", "Smith");
        createUser("Alice", "Roe");

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(notNullValue()))
                .andExpect(jsonPath("$.data[0].firsName").value(notNullValue()))
                .andExpect(jsonPath("$.data[0].lastName").value(notNullValue()));
    }

    @Test
    void getAllUsers_whenNoUsers_shouldReturn200WithEmptyResponse() throws Exception {
        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalItems").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void getAllUsers_withCustomPageAndSize_shouldReturnCorrectMetadata() throws Exception {
        for (int i = 0; i < 11; i++) {
            createUser("User" + (char) ('A' + i), "Test");
        }

        mockMvc.perform(get("/api/users")
                        .param("page", "2")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalItems").value(11))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void getAllUsers_whenPageParamIsInvalid_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("page", "abc")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers_whenSizeParamIsInvalid_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "xyz")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_whenValidPayload_shouldReturn204AndPersistChanges() throws Exception {
        String id = createUser("John", "Doe");

        UserFormDto updateDto = new UserFormDto();
        updateDto.setFirsName("Alice");
        updateDto.setLastName("Smith");

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNoContent());

        Optional<User> updated = userRepository.findById(UUID.fromString(id));
        assertTrue(updated.isPresent());
        assertEquals("Alice", updated.get().getFirsName());
        assertEquals("Smith", updated.get().getLastName());
    }

    @Test
    void updateUser_whenBodyViolatesValidation_shouldReturn400AndNotChangeEntity() throws Exception {
        String id = createUser("John", "Doe");

        UserFormDto invalidDto = new UserFormDto();
        invalidDto.setFirsName(null);
        invalidDto.setLastName(null);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        User existing = userRepository.findById(UUID.fromString(id)).orElseThrow();
        assertEquals("John", existing.getFirsName());
        assertEquals("Doe", existing.getLastName());
    }

    @Test
    void updateUser_whenUserDoesNotExist_shouldReturn404() throws Exception {
        UUID notExistingId = UUID.randomUUID();

        UserFormDto updateDto = new UserFormDto();
        updateDto.setFirsName("Alice");
        updateDto.setLastName("Smith");

        mockMvc.perform(put("/api/users/{id}", notExistingId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_whenPathIdIsInvalidUuid_shouldReturn400() throws Exception {
        UserFormDto updateDto = new UserFormDto();
        updateDto.setFirsName("Alice");
        updateDto.setLastName("Smith");

        mockMvc.perform(put("/api/users/{id}", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_whenInvalidJson_shouldReturn400() throws Exception {
        String id = createUser("Jane", "Roe");
        String invalidJson = "{\"firsName\": \"Alice\", \"lastName\": }";

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        User existing = userRepository.findById(UUID.fromString(id)).orElseThrow();
        assertEquals("Jane", existing.getFirsName());
        assertEquals("Roe", existing.getLastName());
    }

    private String createUser(String firstName, String lastName) throws Exception {
        UserFormDto dto = new UserFormDto();
        dto.setFirsName(firstName);
        dto.setLastName(lastName);

        MvcResult result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        JsonNode created = objectMapper.readTree(result.getResponse().getContentAsString());
        return created.get("id").asText();
    }
}
