package com.reportatucalle.modules.report.infrastructure.controller;

import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.service.ReportService;
import com.reportatucalle.shared.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final ReportService reportService;

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
     *   "radiusInMeters": 3000
     * }
     */
    @PostMapping("/optimize-route")
    public ResponseEntity<ApiResponse<OptimizedRoute>> optimizeRoute(
            @RequestParam Long categoryId,
            @RequestParam Double startLatitude,
            @RequestParam Double startLongitude,
            @RequestParam(defaultValue = "5000") Double radiusInMeters
    ) {
        Optional<OptimizedRoute> result = reportService.getOptimizedRouteForNearbyReports(
                categoryId, startLatitude, startLongitude, radiusInMeters
        );

        return result
                .map(route -> ResponseEntity.ok(
                        ApiResponse.success(route, "Ruta optimizada calculada exitosamente")))
                .orElse(ResponseEntity.ok(
                        ApiResponse.success(null, "Esta categoría no requiere optimización de ruta")));
    }
}