package com.reportatucalle.modules.report.application.service;

import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import com.reportatucalle.modules.optimization.application.service.RouteOptimizationService;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.dto.UpdateReportStatusRequest;
import com.reportatucalle.modules.report.application.dto.AssignReportRequest;
import com.reportatucalle.modules.report.application.mapper.ReportMapper;
import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import com.reportatucalle.modules.report.domain.entity.ReportEndorsement;
import com.reportatucalle.modules.report.domain.portsout.ReportNotificationPort;
import com.reportatucalle.modules.report.domain.repository.ReportEndorsementRepository;
import com.reportatucalle.modules.report.domain.repository.ReportRepository;
import com.reportatucalle.modules.report.application.validation.ReportValidationChain;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import com.reportatucalle.shared.exception.BusinessException;
import com.reportatucalle.modules.report.application.event.ReportEventPublisher;
import com.reportatucalle.modules.report.application.event.ReportCreatedEvent;
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

@Service("reportService")
@RequiredArgsConstructor
public class ReportService implements IReportService {

    private final ReportRepository reportRepository;
    private final ReportEndorsementRepository reportEndorsementRepository;
    private final UserProfileRepository userProfileRepository;
    private final ReportMapper reportMapper;
    private final RouteOptimizationService routeOptimizationService;
    private final ReportNotificationPort notificationPort;
    private final ReportValidationChain reportValidationChain;
    private final ReportEventPublisher eventPublisher;

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
        // Ejecutar la cadena de validación
        reportValidationChain.validate(request);

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
                        .resolutionImageUrl(existingReport.getResolutionImageUrl())
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
        
        notificationPort.notifyReportCreated(savedReport);
        eventPublisher.publish(new ReportCreatedEvent(savedReport));

        return reportMapper.toResponse(savedReport);
    }

    @Transactional
    public ReportResponse updateReport(Long id, com.reportatucalle.modules.report.application.dto.UpdateReportRequest request) {
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UserProfile citizenProfile = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));

        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Reporte no encontrado", "REPORT_NOT_FOUND"));

        if (!existingReport.getCitizenId().equals(citizenProfile.getId())) {
            throw new BusinessException("No tienes permiso para modificar este reporte", "FORBIDDEN");
        }

        Report updatedReport = Report.builder()
                .id(existingReport.getId())
                .citizenId(existingReport.getCitizenId())
                .categoryId(request.categoryId() != null ? request.categoryId() : existingReport.getCategoryId())
                .assignedToUserId(existingReport.getAssignedToUserId())
                .title(request.title() != null ? request.title() : existingReport.getTitle())
                .description(request.description() != null ? request.description() : existingReport.getDescription())
                .imageUrl(existingReport.getImageUrl())
                .resolutionImageUrl(existingReport.getResolutionImageUrl())
                .location(existingReport.getLocation())
                .status(existingReport.getStatus())
                .reportCount(existingReport.getReportCount())
                .createdAt(existingReport.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        return reportMapper.toResponse(reportRepository.save(updatedReport));
    }

    @Transactional
    public void deleteReport(Long id) {
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UserProfile citizenProfile = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));

        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Reporte no encontrado", "REPORT_NOT_FOUND"));

        if (!existingReport.getCitizenId().equals(citizenProfile.getId())) {
            throw new BusinessException("No tienes permiso para eliminar este reporte", "FORBIDDEN");
        }

        reportRepository.deleteById(id);
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
     * @param reportIds        lista de IDs de reportes seleccionados en el frontend
     * @return ruta optimizada con coordenadas ordenadas, o empty si no aplica
     */
    @Transactional(readOnly = true)
    public Optional<OptimizedRoute> getOptimizedRouteForSelectedReports(
            Long categoryId,
            Double startLatitude,
            Double startLongitude,
            List<Long> reportIds
    ) {
        // 1. Obtener reportes específicos solicitados por el frontend
        List<Report> nearbyReports = reportRepository.findAllById(reportIds).stream()
                .filter(report -> report.getCategoryId().equals(categoryId))
                .filter(report -> {
                    String status = report.getStatus().getName();
                    return status.equals("PENDING") || status.equals("IN_PROGRESS") || status.equals("ASSIGNED");
                })
                .collect(Collectors.toList());

        if (nearbyReports.isEmpty()) {
            return Optional.empty();
        }

        // 2. Convertir ubicaciones de reportes a coordenadas del módulo optimization
        // JTS almacena (X=longitud, Y=latitud), optimization usa (latitude, longitude)
        List<Coordinate> destinations = nearbyReports.stream()
                .map(report -> new Coordinate(
                        report.getId(),
                        report.getLocation().getY(), // latitud
                        report.getLocation().getX(), // longitud
                        report.getReportCount() != null ? report.getReportCount() : 1
                ))
                .collect(Collectors.toList());

        // 3. Punto de inicio del supervisor (no tiene reportCount)
        Coordinate startPoint = new Coordinate(null, startLatitude, startLongitude, 1);

        // 4. Delegar al servicio de optimización — él decide qué algoritmo usar según la categoría
        return routeOptimizationService.optimizeRoute(categoryId, startPoint, destinations);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll().stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportResponse getReportById(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Reporte no encontrado", "REPORT_NOT_FOUND"));
        return reportMapper.toResponse(report);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getAssignedReports() {
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UserProfile supervisorProfile = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));
                
        List<ReportStatus> activeStatuses = List.of(
            com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString("PENDING"),
            com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString("IN_PROGRESS"),
            com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString("ASSIGNED")
        );
        
        List<Report> activeReports = reportRepository.findByAssignedToUserIdAndStatusIn(supervisorProfile.getId(), activeStatuses);
        
        List<Report> recentResolved = reportRepository.findTop30ByAssignedToUserIdAndStatusOrderByCreatedAtDesc(
            supervisorProfile.getId(),
            com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString("RESOLVED")
        );
        
        activeReports.addAll(recentResolved);
        
        return activeReports.stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportResponse updateReportStatus(Long id, UpdateReportStatusRequest request) {
        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Reporte no encontrado", "REPORT_NOT_FOUND"));
        
        ReportStatus newStatus = com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString(request.status());
        
        if ("RESOLVED".equals(newStatus.getName()) && !"RESOLVED".equals(existingReport.getStatus().getName())) {
            userProfileRepository.findById(existingReport.getCitizenId()).ifPresent(creator -> {
                userProfileRepository.save(UserProfile.builder()
                        .id(creator.getId())
                        .accountId(creator.getAccountId())
                        .firstName(creator.getFirstName())
                        .lastName(creator.getLastName())
                        .phone(creator.getPhone())
                        .civicScore((creator.getCivicScore() != null ? creator.getCivicScore() : 0) + 10)
                        .createdAt(creator.getCreatedAt())
                        .build());
            });
            
            reportEndorsementRepository.findByReportId(existingReport.getId()).forEach(endorsement -> {
                userProfileRepository.findById(endorsement.getCitizenId()).ifPresent(endorser -> {
                    userProfileRepository.save(UserProfile.builder()
                            .id(endorser.getId())
                            .accountId(endorser.getAccountId())
                            .firstName(endorser.getFirstName())
                            .lastName(endorser.getLastName())
                            .phone(endorser.getPhone())
                            .civicScore((endorser.getCivicScore() != null ? endorser.getCivicScore() : 0) + 5)
                            .createdAt(endorser.getCreatedAt())
                            .build());
                });
            });
        } else if ("RESOLVED".equals(existingReport.getStatus().getName()) && !"RESOLVED".equals(newStatus.getName())) {
            userProfileRepository.findById(existingReport.getCitizenId()).ifPresent(creator -> {
                userProfileRepository.save(UserProfile.builder()
                        .id(creator.getId())
                        .accountId(creator.getAccountId())
                        .firstName(creator.getFirstName())
                        .lastName(creator.getLastName())
                        .phone(creator.getPhone())
                        .civicScore(Math.max(0, (creator.getCivicScore() != null ? creator.getCivicScore() : 0) - 10))
                        .createdAt(creator.getCreatedAt())
                        .build());
            });
            
            reportEndorsementRepository.findByReportId(existingReport.getId()).forEach(endorsement -> {
                userProfileRepository.findById(endorsement.getCitizenId()).ifPresent(endorser -> {
                    userProfileRepository.save(UserProfile.builder()
                            .id(endorser.getId())
                            .accountId(endorser.getAccountId())
                            .firstName(endorser.getFirstName())
                            .lastName(endorser.getLastName())
                            .phone(endorser.getPhone())
                            .civicScore(Math.max(0, (endorser.getCivicScore() != null ? endorser.getCivicScore() : 0) - 5))
                            .createdAt(endorser.getCreatedAt())
                            .build());
                });
            });
        }
        
        Report updatedReport = Report.builder()
                .id(existingReport.getId())
                .citizenId(existingReport.getCitizenId())
                .categoryId(existingReport.getCategoryId())
                .assignedToUserId(existingReport.getAssignedToUserId())
                .title(existingReport.getTitle())
                .description(existingReport.getDescription())
                .imageUrl(existingReport.getImageUrl())
                .resolutionImageUrl(request.resolutionImageUrl() != null ? request.resolutionImageUrl() : existingReport.getResolutionImageUrl())
                .location(existingReport.getLocation())
                .status(newStatus)
                .reportCount(existingReport.getReportCount())
                .createdAt(existingReport.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
                
        Report savedReport = reportRepository.save(updatedReport);
        notificationPort.notifyReportStatusUpdated(savedReport);
        return reportMapper.toResponse(savedReport);
    }

    @Transactional
    public ReportResponse assignReport(Long id, AssignReportRequest request) {
        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Reporte no encontrado", "REPORT_NOT_FOUND"));
        
        Report updatedReport = Report.builder()
                .id(existingReport.getId())
                .citizenId(existingReport.getCitizenId())
                .categoryId(existingReport.getCategoryId())
                .assignedToUserId(request.assignedToUserId())
                .title(existingReport.getTitle())
                .description(existingReport.getDescription())
                .imageUrl(existingReport.getImageUrl())
                .resolutionImageUrl(existingReport.getResolutionImageUrl())
                .location(existingReport.getLocation())
                .status(request.assignedToUserId() == null ? 
                        com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString("PENDING") : 
                        com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString("ASSIGNED"))
                .reportCount(existingReport.getReportCount())
                .createdAt(existingReport.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
                
        Report savedReport = reportRepository.save(updatedReport);
        notificationPort.notifyReportAssigned(savedReport);
        return reportMapper.toResponse(savedReport);
    }

    @Transactional
    public ReportResponse selfAssignReport(Long id) {
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UserProfile supervisorProfile = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));

        Report existingReport = reportRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Reporte no encontrado", "REPORT_NOT_FOUND"));

        if (!"PENDING".equals(existingReport.getStatus().getName())) {
            throw new BusinessException("El reporte ya no está PENDING", "INVALID_STATUS");
        }

        Report updatedReport = Report.builder()
                .id(existingReport.getId())
                .citizenId(existingReport.getCitizenId())
                .categoryId(existingReport.getCategoryId())
                .assignedToUserId(supervisorProfile.getId())
                .title(existingReport.getTitle())
                .description(existingReport.getDescription())
                .imageUrl(existingReport.getImageUrl())
                .resolutionImageUrl(existingReport.getResolutionImageUrl())
                .location(existingReport.getLocation())
                .status(com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString("ASSIGNED"))
                .reportCount(existingReport.getReportCount())
                .createdAt(existingReport.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
                
        Report savedReport = reportRepository.save(updatedReport);
        notificationPort.notifyReportAssigned(savedReport);
        return reportMapper.toResponse(savedReport);
    }

    @Transactional
    public List<ReportResponse> bulkSelfAssignReports(List<Long> reportIds) {
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        UserProfile supervisorProfile = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));

        List<Report> savedReports = new java.util.ArrayList<>();

        for (Long id : reportIds) {
            reportRepository.findById(id).ifPresent(existingReport -> {
                if ("PENDING".equals(existingReport.getStatus().getName())) {
                    Report updatedReport = Report.builder()
                            .id(existingReport.getId())
                            .citizenId(existingReport.getCitizenId())
                            .categoryId(existingReport.getCategoryId())
                            .assignedToUserId(supervisorProfile.getId())
                            .title(existingReport.getTitle())
                            .description(existingReport.getDescription())
                            .imageUrl(existingReport.getImageUrl())
                            .resolutionImageUrl(existingReport.getResolutionImageUrl())
                            .location(existingReport.getLocation())
                            .status(com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString("ASSIGNED"))
                            .reportCount(existingReport.getReportCount())
                            .createdAt(existingReport.getCreatedAt())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    Report savedReport = reportRepository.save(updatedReport);
                    notificationPort.notifyReportAssigned(savedReport);
                    savedReports.add(savedReport);
                }
            });
        }

        return savedReports.stream()
                .map(reportMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReportResponse endorseReport(Long reportId) {
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserProfile citizenProfile = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));

        Report existingReport = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException("Reporte no encontrado", "REPORT_NOT_FOUND"));

        Long citizenId = citizenProfile.getId();

        boolean isPioneer = existingReport.getCitizenId().equals(citizenId);
        boolean alreadyEndorsed = reportEndorsementRepository
                .existsByReportIdAndCitizenId(existingReport.getId(), citizenId);

        if (!isPioneer && !alreadyEndorsed) {
            ReportEndorsement endorsement = ReportEndorsement.builder()
                    .reportId(existingReport.getId())
                    .citizenId(citizenId)
                    .build();
            reportEndorsementRepository.save(endorsement);

            Report updatedReport = Report.builder()
                    .id(existingReport.getId())
                    .citizenId(existingReport.getCitizenId())
                    .categoryId(existingReport.getCategoryId())
                    .assignedToUserId(existingReport.getAssignedToUserId())
                    .title(existingReport.getTitle())
                    .description(existingReport.getDescription())
                    .imageUrl(existingReport.getImageUrl())
                    .resolutionImageUrl(existingReport.getResolutionImageUrl())
                    .location(existingReport.getLocation())
                    .status(existingReport.getStatus())
                    .reportCount(existingReport.getReportCount() + 1)
                    .createdAt(existingReport.getCreatedAt())
                    .updatedAt(LocalDateTime.now())
                    .build();
                    
            existingReport = reportRepository.save(updatedReport);
            notificationPort.notifyReportStatusUpdated(existingReport);
        } else {
            throw new BusinessException("Ya has respaldado este reporte o eres el creador", "ALREADY_ENDORSED");
        }

        return reportMapper.toResponse(existingReport);
    }
}