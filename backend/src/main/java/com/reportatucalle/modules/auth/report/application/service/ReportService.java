package com.reportatucalle.modules.report.application.service;

import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import com.reportatucalle.modules.optimization.application.service.RouteOptimizationService;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.mapper.ReportMapper;
import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportEndorsement;
import com.reportatucalle.modules.report.domain.repository.ReportEndorsementRepository;
import com.reportatucalle.modules.report.domain.repository.ReportRepository;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import com.reportatucalle.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportEndorsementRepository reportEndorsementRepository;
    private final UserProfileRepository userProfileRepository;
    private final ReportMapper reportMapper;
    private final RouteOptimizationService routeOptimizationService;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private static final double DEDUPLICATION_RADIUS_METERS = 20.0;

    /**
     * Crea un nuevo reporte ciudadano o consolida uno existente.
     *
     * Flujo de deduplicación inteligente:
     * - Si existe un reporte de la misma categoría en un radio de 20m, se consolida.
     * - Si el ciudadano es el creador original o ya votó antes (anti-spam), se bloquea.
     * - Si es un ciudadano nuevo reportando el mismo problema, se registra su endorsement
     *   y se incrementa el contador de severidad del reporte existente.
     * - Si no existe duplicado, se crea un nuevo reporte (flujo pionero).
     */
    @Transactional
    public ReportResponse createReport(CreateReportRequest request) {

        // Obtener la cuenta autenticada desde el contexto de seguridad
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        // Recuperar el perfil del ciudadano via puerto de dominio
        UserProfile citizenProfile = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));

        // JTS exige (X, Y) -> (Longitud, Latitud) para el plano geoespacial
        org.locationtech.jts.geom.Coordinate coordinate =
                new org.locationtech.jts.geom.Coordinate(request.longitude(), request.latitude());
        Point targetLocation = geometryFactory.createPoint(coordinate);

        // Buscar si existe un reporte duplicado en el radio de deduplicación
        Optional<Report> existingReportOpt = reportRepository.findExistingDuplicate(
                request.categoryId(),
                targetLocation,
                DEDUPLICATION_RADIUS_METERS
        );

        if (existingReportOpt.isPresent()) {
            Report existingReport = existingReportOpt.get();
            Long citizenId = citizenProfile.getId();

            // REGLA ANTI-SPAM: El creador original o alguien que ya votó no puede volver a sumar puntos.
            boolean isPioneer = existingReport.getCitizenId().equals(citizenId);
            boolean alreadyEndorsed = reportEndorsementRepository
                    .existsByReportIdAndCitizenId(existingReport.getId(), citizenId);

            if (!isPioneer && !alreadyEndorsed) {
                // 1. Registramos al ciudadano como seguidor (Patrón Observer en BD)
                ReportEndorsement endorsement = ReportEndorsement.builder()
                        .reportId(existingReport.getId())
                        .citizenId(citizenId)
                        .build();
                reportEndorsementRepository.save(endorsement);

                // 2. Incrementamos la severidad usando el builder (inmutabilidad del dominio)
                // No usamos setter porque Report es inmutable — creamos una nueva instancia
                existingReport = Report.builder()
                        .id(existingReport.getId())
                        .citizenId(existingReport.getCitizenId())
                        .categoryId(existingReport.getCategoryId())
                        .title(existingReport.getTitle())
                        .description(existingReport.getDescription())
                        .imageUrl(existingReport.getImageUrl())
                        .location(existingReport.getLocation())
                        .status(existingReport.getStatus())
                        .reportCount(existingReport.getReportCount() + 1)
                        .createdAt(existingReport.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build();
                existingReport = reportRepository.save(existingReport);
            }

            // Devolvemos el reporte existente — haya sumado voto o haya sido bloqueado
            // por spam, para el usuario es un éxito visual en ambos casos
            return reportMapper.toResponse(existingReport);
        }

        // FLUJO PIONERO: No existe duplicado, crear nuevo reporte
        Report newReport = reportMapper.toEntity(request, citizenProfile.getId());
        Report savedReport = reportRepository.save(newReport);

        return reportMapper.toResponse(savedReport);
    }

    /**
     * Recupera todos los reportes activos dentro de un radio geográfico específico.
     * Usa índices espaciales GiST de PostGIS para búsquedas eficientes.
     */
    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsNearby(Double latitude, Double longitude, Double radiusInMeters) {

        // JTS exige (X, Y) -> (Longitud, Latitud) para el plano geoespacial
        org.locationtech.jts.geom.Coordinate coordinate =
                new org.locationtech.jts.geom.Coordinate(longitude, latitude);
        Point centerPoint = geometryFactory.createPoint(coordinate);

        // Ejecuta la consulta nativa de PostGIS con índices espaciales GiST
        List<Report> reports = reportRepository.findReportsWithinRadius(centerPoint, radiusInMeters);

        // Traduce la lista de entidades de dominio a DTOs de salida para el frontend
        return reports.stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calcula la ruta óptima para atender los reportes cercanos a una ubicación.
     *
     * Flujo:
     * 1. Obtiene los reportes activos dentro del radio indicado
     * 2. Convierte sus ubicaciones a coordenadas del módulo optimization
     * 3. Delega al RouteOptimizationService con el algoritmo de la categoría
     * 4. Retorna la ruta ordenada, o empty si la categoría no requiere optimización
     *
     * @param categoryId       categoría que define qué algoritmo usar
     * @param startLatitude    latitud del punto de inicio del supervisor
     * @param startLongitude   longitud del punto de inicio del supervisor
     * @param radiusInMeters   radio de búsqueda de reportes
     * @return ruta optimizada con coordenadas ordenadas, o empty si no aplica
     */
    @Transactional(readOnly = true)
    public Optional<OptimizedRoute> getOptimizedRouteForNearbyReports(
            Long categoryId,
            Double startLatitude,
            Double startLongitude,
            Double radiusInMeters
    ) {
        // 1. Obtener reportes cercanos dentro del radio
        org.locationtech.jts.geom.Coordinate coord =
                new org.locationtech.jts.geom.Coordinate(startLongitude, startLatitude);
        Point centerPoint = geometryFactory.createPoint(coord);
        List<Report> nearbyReports = reportRepository.findReportsWithinRadius(centerPoint, radiusInMeters);

        if (nearbyReports.isEmpty()) {
            return Optional.empty();
        }

        // 2. Convertir ubicaciones de reportes a coordenadas del módulo optimization
        // JTS almacena (X=longitud, Y=latitud), optimization usa (latitude, longitude)
        List<Coordinate> destinations = nearbyReports.stream()
                .map(report -> new Coordinate(
                        report.getId(),
                        report.getLocation().getY(), // latitud
                        report.getLocation().getX()  // longitud
                ))
                .collect(Collectors.toList());

        // 3. Punto de inicio del supervisor
        Coordinate startPoint = new Coordinate(null, startLatitude, startLongitude);

        // 4. Delegar al servicio de optimización — él decide qué algoritmo usar según la categoría
        return routeOptimizationService.optimizeRoute(categoryId, startPoint, destinations);
    }
}