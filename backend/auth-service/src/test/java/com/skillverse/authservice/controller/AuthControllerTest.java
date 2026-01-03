package com.skillverse.authservice.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillverse.authservice.dto.AuthRequest;
import com.skillverse.authservice.dto.RegisterRequest;
import com.skillverse.authservice.model.User;
import com.skillverse.authservice.service.AuthService;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void register_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .username("testuser")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        User mockUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(authService.register(any(), any(), any(), any(), any())).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void login_ShouldReturnAuthResponse() throws Exception {
        // Arrange
        AuthRequest request = new AuthRequest("test@example.com", "password123");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("accessToken", "mock-token");
        mockResponse.put("refreshToken", "mock-refresh");
        mockResponse.put("expiresIn", 3600);
        mockResponse.put("userId", UUID.randomUUID());

        when(authService.login(any(), any())).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mock-token"))
                .andExpect(jsonPath("$.refreshToken").value("mock-refresh"));
    }

    @Test
    void validateToken_ShouldReturnValidationResult() throws Exception {
        // Arrange
        when(authService.validateToken(any())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/validate")
                .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }
}
