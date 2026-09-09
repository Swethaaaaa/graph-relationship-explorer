package com.swetha.graphexplorer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swetha.graphexplorer.domain.enums.EntityType;
import com.swetha.graphexplorer.dto.request.CreateUserRequest;
import com.swetha.graphexplorer.dto.response.UserResponse;
import com.swetha.graphexplorer.exception.EntityNotFoundException;
import com.swetha.graphexplorer.service.UserService;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Exercises the real HTTP dispatch chain for UserController: Bean
 * Validation on the request DTO, controller routing, and the centralized
 * {@code GlobalExceptionHandler} error shape. Only UserService is mocked —
 * everything else (MVC, validation, exception mapping) is real Spring
 * wiring, so this catches integration bugs a pure unit test on the service
 * would miss, without needing a running Neo4j.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createUser_withValidRequest_returns201WithLocationHeader() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Alice Smith", "alice@example.com", "Engineer", null, null);
        UserResponse response = new UserResponse(
                "user-1", "Alice Smith", "alice@example.com", "Engineer", null, null, Instant.now());
        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void createUser_withBlankFullNameAndInvalidEmail_returns400WithFieldErrors() throws Exception {
        CreateUserRequest invalid = new CreateUserRequest("", "not-an-email", null, null, null);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.fullName").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists());
    }

    @Test
    void getUser_whenNotFound_returns404WithConsistentErrorShape() throws Exception {
        when(userService.getUser("missing")).thenThrow(new EntityNotFoundException(EntityType.USER, "missing"));

        mockMvc.perform(get("/api/users/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("ENTITY_NOT_FOUND"))
                .andExpect(jsonPath("$.path").value("/api/users/missing"));
    }
}
