package com.reportatucalle.modules.auth.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reportatucalle.modules.auth.application.dto.AuthAccountResponse;
import com.reportatucalle.modules.auth.application.dto.AuthResponse;
import com.reportatucalle.modules.auth.application.dto.LoginRequest;
import com.reportatucalle.modules.auth.application.dto.RegisterRequest;
import com.reportatucalle.modules.auth.application.dto.UpdateRoleRequest;
import com.reportatucalle.modules.auth.application.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("Should successfully register a new account")
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@example.com", "password123");
        AuthResponse response = new AuthResponse("mocked-jwt-token");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Ciudadano registrado exitosamente"))
                .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"));
    }

    @Test
    @DisplayName("Should fail registration on invalid request (Validation)")
    void register_ValidationError() throws Exception {
        RegisterRequest request = new RegisterRequest("", "Doe", "invalid-email", "pass");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully login")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest("john@example.com", "password123");
        AuthResponse response = new AuthResponse("mocked-jwt-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Inicio de sesión exitoso"))
                .andExpect(jsonPath("$.data.token").value("mocked-jwt-token"));
    }

    @Test
    @DisplayName("Should fail login on invalid request (Validation)")
    void login_ValidationError() throws Exception {
        LoginRequest request = new LoginRequest("not-an-email", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully get all accounts")
    @WithMockUser(roles = "ADMIN")
    void getAllAccounts_Success() throws Exception {
        AuthAccountResponse account1 = new AuthAccountResponse(1L, "admin@example.com", "ADMIN", LocalDateTime.now());
        AuthAccountResponse account2 = new AuthAccountResponse(2L, "user@example.com", "CITIZEN", LocalDateTime.now());

        when(authService.getAllAccounts()).thenReturn(List.of(account1, account2));

        mockMvc.perform(get("/api/v1/auth/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cuentas recuperadas exitosamente"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$.data[1].email").value("user@example.com"));
    }

    @Test
    @DisplayName("Should successfully update role")
    @WithMockUser(roles = "ADMIN")
    void updateRole_Success() throws Exception {
        UpdateRoleRequest request = new UpdateRoleRequest("OPERATOR");
        AuthAccountResponse response = new AuthAccountResponse(2L, "user@example.com", "OPERATOR", LocalDateTime.now());

        when(authService.updateRole(eq(2L), any(UpdateRoleRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/auth/accounts/{id}/role", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Rol actualizado exitosamente"))
                .andExpect(jsonPath("$.data.role").value("OPERATOR"));
    }
}
