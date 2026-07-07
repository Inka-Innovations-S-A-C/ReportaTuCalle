package com.reportatucalle.modules.user.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reportatucalle.modules.user.application.dto.UpdateProfileRequest;
import com.reportatucalle.modules.user.application.dto.UserProfileResponse;
import com.reportatucalle.modules.user.application.dto.UserProfileSummaryResponse;
import com.reportatucalle.modules.user.application.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private com.reportatucalle.shared.security.JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetMyProfile() throws Exception {
        UserProfileResponse response = new UserProfileResponse(
                1L, "John Doe", "John", "Doe", "john.doe@example.com", "CITIZEN", "+1234567890", 10, LocalDateTime.now()
        );

        when(userService.getMyProfile()).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"));
    }

    @Test
    void shouldUpdateMyProfile() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("Jane", "Doe", "+1987654321");
        UserProfileResponse response = new UserProfileResponse(
                1L, "Jane Doe", "Jane", "Doe", "jane.doe@example.com", "CITIZEN", "+1987654321", 15, LocalDateTime.now()
        );

        when(userService.updateMyProfile(any(UpdateProfileRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName").value("Jane"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.phone").value("+1987654321"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdateMyProfileIsInvalid() throws Exception {
        UpdateProfileRequest request = new UpdateProfileRequest("", "Doe", "+1987654321");

        mockMvc.perform(put("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        List<UserProfileSummaryResponse> response = List.of(
                new UserProfileSummaryResponse(1L, 1L, "John Doe", "John", "Doe", "+1234567890", 10, LocalDateTime.now())
        );

        when(userService.getAllUsers()).thenReturn(response);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].firstName").value("John"));
    }

    @Test
    void shouldGetLeaderboard() throws Exception {
        List<UserProfileSummaryResponse> response = List.of(
                new UserProfileSummaryResponse(1L, 1L, "John Doe", "John", "Doe", "+1234567890", 100, LocalDateTime.now())
        );

        when(userService.getLeaderboard()).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].civicScore").value(100));
    }
}
