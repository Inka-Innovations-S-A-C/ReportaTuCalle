package com.reportatucalle.modules.report.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.report.application.dto.*;
import com.reportatucalle.modules.report.application.service.IReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.reportatucalle.shared.security.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IReportService reportService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "CITIZEN")
    public void testCreateReport() throws Exception {
        CreateReportRequest request = new CreateReportRequest(
                1L, "Test Title", "Test Description", -12.0, -77.0, "http://image.url"
        );
        ReportResponse response = new ReportResponse(1L, 1L, 1L, null, "Test Title", "Test Description", "http://image.url", null, "PENDING", 1, -12.0, -77.0, null);

        when(reportService.createReport(any(CreateReportRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/reports").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(roles = "CITIZEN")
    public void testUpdateReport() throws Exception {
        String content = "{\"title\":\"Updated Title\",\"description\":\"Updated Description\",\"categoryId\":2}";

        ReportResponse response = new ReportResponse(1L, 1L, 2L, null, "Updated Title", "Updated Description", null, null, "PENDING", 1, -12.0, -77.0, null);

        when(reportService.updateReport(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/reports/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteReport() throws Exception {
        mockMvc.perform(delete("/api/v1/reports/1").with(csrf()))
                .andExpect(status().isOk());

        verify(reportService).deleteReport(1L);
    }

    @Test
    public void testGetReportsNearby() throws Exception {
        when(reportService.getReportsNearby(-12.0, -77.0, 5000.0)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reports/nearby")
                .param("latitude", "-12.0")
                .param("longitude", "-77.0")
                .param("radius", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "SUPERVISOR")
    public void testOptimizeRoute() throws Exception {
        OptimizeRouteRequest request = new OptimizeRouteRequest(1L, -12.0, -77.0, Arrays.asList(1L, 2L));
        OptimizedRoute route = new OptimizedRoute(Collections.emptyList(), 0.0, Collections.emptyMap());
        
        when(reportService.getOptimizedRouteForSelectedReports(1L, -12.0, -77.0, Arrays.asList(1L, 2L)))
                .thenReturn(Optional.of(route));

        mockMvc.perform(post("/api/v1/reports/optimize-route").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testGetAllReports() throws Exception {
        when(reportService.getAllReports()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testGetReportById() throws Exception {
        ReportResponse response = new ReportResponse(
                1L, 1L, 1L, null, "Title", "Desc",
                null, null, "PENDING", 1, -12.0, -77.0, LocalDateTime.now()
        );

        when(reportService.getReportById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(roles = "SUPERVISOR")
    public void testGetAssignedReports() throws Exception {
        when(reportService.getAssignedReports()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/reports/assigned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "SUPERVISOR")
    public void testUpdateReportStatus() throws Exception {
        String content = "{\"status\":\"IN_PROGRESS\"}";
        
        ReportResponse response = new ReportResponse(1L, 1L, 1L, null, "Title", "Desc", null, null, "IN_PROGRESS", 1, -12.0, -77.0, null);

        when(reportService.updateReportStatus(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/reports/1/status").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAssignReport() throws Exception {
        String content = "{\"supervisorId\":10}";
        
        ReportResponse response = new ReportResponse(1L, 1L, 1L, null, "Title", "Desc", null, null, "PENDING", 1, -12.0, -77.0, null);

        when(reportService.assignReport(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/reports/1/assign").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(roles = "SUPERVISOR")
    public void testSelfAssignReport() throws Exception {
        ReportResponse response = new ReportResponse(
                1L, 1L, 1L, 20L, "Title", "Desc",
                null, null, "PENDING", 1, -12.0, -77.0, LocalDateTime.now()
        );

        when(reportService.selfAssignReport(1L)).thenReturn(response);

        mockMvc.perform(put("/api/v1/reports/1/self-assign").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @WithMockUser(roles = "SUPERVISOR")
    public void testBulkSelfAssignReports() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        when(reportService.bulkSelfAssignReports(ids)).thenReturn(Collections.emptyList());

        mockMvc.perform(put("/api/v1/reports/bulk-assign").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(roles = "CITIZEN")
    public void testEndorseReport() throws Exception {
        ReportResponse response = new ReportResponse(
                1L, 1L, 1L, null, "Title", "Desc",
                null, null, "PENDING", 2, -12.0, -77.0, LocalDateTime.now()
        );

        when(reportService.endorseReport(1L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/reports/1/endorse").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }
}
