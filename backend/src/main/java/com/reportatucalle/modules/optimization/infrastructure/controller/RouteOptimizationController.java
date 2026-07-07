package com.reportatucalle.modules.optimization.infrastructure.controller;

import com.reportatucalle.modules.optimization.application.dto.RouteHistoryResponse;
import com.reportatucalle.modules.optimization.application.dto.SaveRouteRequest;
import com.reportatucalle.modules.optimization.application.dto.UpdateRouteStatusRequest;
import com.reportatucalle.modules.optimization.application.service.RouteOptimizationService;
import com.reportatucalle.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteOptimizationController {

    private final RouteOptimizationService service;

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<RouteHistoryResponse>> saveRoute(
            @Valid @RequestBody SaveRouteRequest request
    ) {
        RouteHistoryResponse response = service.saveRoute(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Ruta guardada exitosamente"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<RouteHistoryResponse>>> getRoutesBySupervisor(
            @RequestParam Long supervisorId
    ) {
        List<RouteHistoryResponse> response = service.getRoutesBySupervisorId(supervisorId);
        return ResponseEntity.ok(ApiResponse.success(response, "Rutas recuperadas exitosamente"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<RouteHistoryResponse>> updateRouteStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRouteStatusRequest request
    ) {
        RouteHistoryResponse response = service.updateRouteStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Estado de la ruta actualizado exitosamente"));
    }
}
