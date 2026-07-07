package com.reportatucalle.modules.optimization.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reportatucalle.modules.optimization.application.dto.RouteHistoryResponse;
import com.reportatucalle.modules.optimization.application.dto.SaveRouteRequest;
import com.reportatucalle.modules.optimization.application.dto.UpdateRouteStatusRequest;
import com.reportatucalle.modules.optimization.application.service.RouteOptimizationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.reportatucalle.shared.security.JwtAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteOptimizationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class RouteOptimizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RouteOptimizationService routeOptimizationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSaveRoute() throws Exception {
        SaveRouteRequest request = new SaveRouteRequest(1L, 2L, "Route", 10.0);
        RouteHistoryResponse response = new RouteHistoryResponse(1L, 1L, 2L, "Route", 10.0, "PENDING", null);

        when(routeOptimizationService.saveRoute(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/routes/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testGetRoutesBySupervisor() throws Exception {
        RouteHistoryResponse response = new RouteHistoryResponse(1L, 1L, 2L, "Route", 10.0, "PENDING", null);
        when(routeOptimizationService.getRoutesBySupervisorId(1L)).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/v1/routes")
                .param("supervisorId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testUpdateRouteStatus() throws Exception {
        UpdateRouteStatusRequest request = new UpdateRouteStatusRequest("COMPLETED");
        RouteHistoryResponse response = new RouteHistoryResponse(1L, 1L, 2L, "Route", 10.0, "COMPLETED", null);

        when(routeOptimizationService.updateRouteStatus(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/routes/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }
}
