package com.reportatucalle.modules.report.infrastructure.controller;

import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.OptimizeRouteRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.dto.UpdateReportStatusRequest;
import com.reportatucalle.modules.report.application.dto.AssignReportRequest;
import com.reportatucalle.modules.report.application.service.IReportService;
import com.reportatucalle.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de entrada web para el módulo de reportes.
 * Expone la API para que los ciudadanos interactúen con el mapa.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportService;

    /**
     * Endpoint para que un ciudadano logueado envíe una nueva incidencia.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(
            @Valid @RequestBody CreateReportRequest request
    ) {
        ReportResponse response = reportService.createReport(request);

        // Devolvemos HTTP 201 (Created) y el mensaje de éxito en la capa web
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Reporte ciudadano creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportResponse>> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody com.reportatucalle.modules.report.application.dto.UpdateReportRequest request) {
        ReportResponse response = reportService.updateReport(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Reporte actualizado con éxito"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reporte eliminado con éxito"));
    }

    /**
     * Endpoint para obtener los reportes cercanos a una ubicación.
     * Accesible tanto para ciudadanos como para el mapa principal de la web.
     * Ejemplo: GET /api/v1/reports/nearby?latitude=-12.046&longitude=-77.042&radius=2000
     */
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getReportsNearby(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5000") Double radius
    ) {
        List<ReportResponse> response = reportService.getReportsNearby(latitude, longitude, radius);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Reportes cercanos recuperados exitosamente")
        );
    }

    /**
     * Endpoint para calcular la ruta óptima para atender reportes cercanos.
     * 
     * El supervisor indica su posición actual y el radio de trabajo.
     * El sistema devuelve el orden óptimo para visitar los reportes,
     * usando el algoritmo correspondiente a la categoría indicada.
     * 
     * Ejemplo: POST /api/v1/reports/optimize-route
     * {
     *   "categoryId": 1,
     *   "startLatitude": -12.046,
     *   "startLongitude": -77.042,
     *   "reportIds": [10, 15, 23]
     * }
     */
    @PostMapping("/optimize-route")
    public ResponseEntity<ApiResponse<OptimizedRoute>> optimizeRoute(
            @RequestBody OptimizeRouteRequest request
    ) {
        Optional<OptimizedRoute> result = reportService.getOptimizedRouteForSelectedReports(
                request.categoryId(), request.startLatitude(), request.startLongitude(), request.reportIds()
        );

        return result
                .map(route -> ResponseEntity.ok(
                        ApiResponse.success(route, "Ruta optimizada calculada exitosamente")))
                .orElse(ResponseEntity.ok(
                        ApiResponse.success(null, "Esta categoría no requiere optimización de ruta")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getAllReports() {
        List<ReportResponse> response = reportService.getAllReports();
        return ResponseEntity.ok(ApiResponse.success(response, "Reportes recuperados exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportResponse>> getReportById(@PathVariable Long id) {
        ReportResponse response = reportService.getReportById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Reporte recuperado exitosamente"));
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getAssignedReports() {
        List<ReportResponse> response = reportService.getAssignedReports();
        return ResponseEntity.ok(ApiResponse.success(response, "Reportes asignados recuperados exitosamente"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReportResponse>> updateReportStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReportStatusRequest request
    ) {
        ReportResponse response = reportService.updateReportStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Estado del reporte actualizado exitosamente"));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReportResponse>> assignReport(
            @PathVariable Long id,
            @Valid @RequestBody AssignReportRequest request
    ) {
        ReportResponse response = reportService.assignReport(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Reporte asignado exitosamente"));
    }

    @PutMapping("/{id}/self-assign")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<ReportResponse>> selfAssignReport(@PathVariable Long id) {
        ReportResponse response = reportService.selfAssignReport(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Reporte auto-asignado exitosamente"));
    }

    @PutMapping("/bulk-assign")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> bulkSelfAssignReports(@RequestBody List<Long> reportIds) {
        List<ReportResponse> response = reportService.bulkSelfAssignReports(reportIds);
        return ResponseEntity.ok(ApiResponse.success(response, "Reportes auto-asignados exitosamente"));
    }

    @PostMapping("/{id}/endorse")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<ApiResponse<ReportResponse>> endorseReport(@PathVariable Long id) {
        ReportResponse response = reportService.endorseReport(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Reporte respaldado exitosamente"));
    }
}