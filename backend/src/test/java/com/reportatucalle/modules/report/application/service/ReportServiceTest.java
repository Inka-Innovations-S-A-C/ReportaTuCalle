package com.reportatucalle.modules.report.application.service;

import com.reportatucalle.modules.auth.domain.entity.Role;
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
import com.reportatucalle.modules.report.domain.entity.ReportEndorsement;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import com.reportatucalle.modules.report.domain.portsout.ReportNotificationPort;
import com.reportatucalle.modules.report.domain.repository.ReportEndorsementRepository;
import com.reportatucalle.modules.report.domain.repository.ReportRepository;
import com.reportatucalle.modules.report.application.validation.ReportValidationChain;
import com.reportatucalle.modules.report.application.event.ReportEventPublisher;
import com.reportatucalle.modules.report.application.event.ReportCreatedEvent;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    private ReportRepository reportRepository;
    private ReportEndorsementRepository endorsementRepository;
    private UserProfileRepository userProfileRepository;
    private RouteOptimizationService routeOptimizationService;
    private ReportNotificationPort notificationPort;
    private ReportValidationChain reportValidationChain;
    private ReportEventPublisher eventPublisher;
    private ReportService service;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @BeforeEach
    void setUp() {
        reportRepository = mock(ReportRepository.class);
        endorsementRepository = mock(ReportEndorsementRepository.class);
        userProfileRepository = mock(UserProfileRepository.class);
        routeOptimizationService = mock(RouteOptimizationService.class);
        notificationPort = mock(ReportNotificationPort.class);
        reportValidationChain = mock(ReportValidationChain.class);
        eventPublisher = mock(ReportEventPublisher.class);
        service = new ReportService(reportRepository, endorsementRepository, userProfileRepository,
                new ReportMapper(), routeOptimizationService, notificationPort, reportValidationChain, eventPublisher);
        AuthAccountJpaEntity account = AuthAccountJpaEntity.builder().id(100L).email("ana@mail.com")
                .password("x").role(Role.CITIZEN).build();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(account, null));
        when(userProfileRepository.findByAccountId(100L)).thenReturn(Optional.of(
                UserProfile.builder().id(50L).accountId(100L).firstName("Ana").lastName("Torres").build()
        ));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createReport_whenNoDuplicate_savesNewReport() {
        CreateReportRequest request = request();
        when(reportRepository.findExistingDuplicate(eq(2L), any(), eq(20.0))).thenReturn(Optional.empty());
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> {
            Report r = invocation.getArgument(0);
            return Report.builder().id(1L).citizenId(r.getCitizenId()).categoryId(r.getCategoryId())
                    .title(r.getTitle()).description(r.getDescription()).imageUrl(r.getImageUrl())
                    .location(r.getLocation()).status(r.getStatus()).reportCount(r.getReportCount())
                    .createdAt(r.getCreatedAt()).updatedAt(r.getUpdatedAt()).build();
        });

        ReportResponse response = service.createReport(request);

        assertEquals(1L, response.id());
        assertEquals("Bache", response.title());
        verify(endorsementRepository, never()).save(any());
        verify(notificationPort).notifyReportCreated(any(Report.class));
        verify(eventPublisher).publish(any(ReportCreatedEvent.class));
    }

    @Test
    void createReport_whenDuplicateAndNewCitizen_addsEndorsementAndIncrementsCount() {
        Report existing = report(9L, 40L, 2);
        when(reportRepository.findExistingDuplicate(eq(2L), any(), eq(20.0))).thenReturn(Optional.of(existing));
        when(endorsementRepository.existsByReportIdAndCitizenId(9L, 50L)).thenReturn(false);
        when(endorsementRepository.save(any(ReportEndorsement.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reportRepository.save(any(Report.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReportResponse response = service.createReport(request());

        assertEquals(3, response.reportCount());
        verify(endorsementRepository).save(any(ReportEndorsement.class));
        verify(reportRepository).save(any(Report.class));
        verify(notificationPort, never()).notifyReportCreated(any(Report.class)); // Not called for duplicate update
    }

    @Test
    void createReport_whenDuplicateFromSameCitizen_doesNotIncrement() {
        Report existing = report(9L, 50L, 2);
        when(reportRepository.findExistingDuplicate(eq(2L), any(), eq(20.0))).thenReturn(Optional.of(existing));

        ReportResponse response = service.createReport(request());

        assertEquals(2, response.reportCount());
        verify(endorsementRepository, never()).save(any());
        verify(reportRepository, never()).save(any());
    }

    @Test
    void getReportsNearby_mapsRepositoryResults() {
        when(reportRepository.findReportsWithinRadius(any(), eq(100.0))).thenReturn(List.of(report(1L, 50L, 1), report(2L, 60L, 1)));

        List<ReportResponse> responses = service.getReportsNearby(-12.0, -77.0, 100.0);

        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).id());
    }

    @Test
    void getOptimizedRouteForSelectedReports_WithoutReports_ShouldReturnEmpty() {
        when(reportRepository.findAllById(anyList())).thenReturn(List.of());

        assertTrue(service.getOptimizedRouteForSelectedReports(1L, -12.0464, -77.0428, List.of()).isEmpty());
    }

    @Test
    void getOptimizedRouteForSelectedReports_WithNearbyReports_ShouldReturnRoute() {
        Report mockReport = report(1L, 50L, 1);
        OptimizedRoute mockOptimizedRoute = new OptimizedRoute(List.of(new Coordinate(1L, -12.0, -77.0, 1)), 0.5, java.util.Map.of());
        when(reportRepository.findAllById(anyList())).thenReturn(List.of(mockReport));
        when(routeOptimizationService.optimizeRoute(anyLong(), any(), anyList())).thenReturn(Optional.of(mockOptimizedRoute));

        Optional<OptimizedRoute> route = service.getOptimizedRouteForSelectedReports(2L, -12.0464, -77.0428, List.of(1L));

        assertEquals(Optional.of(mockOptimizedRoute), route);
    }

    @Test
    void getAllReports_returnsAllReports() {
        when(reportRepository.findAll()).thenReturn(List.of(report(1L, 10L, 1), report(2L, 20L, 2)));
        List<ReportResponse> result = service.getAllReports();
        assertEquals(2, result.size());
    }

    @Test
    void getReportById_whenExists_returnsReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report(1L, 10L, 1)));
        ReportResponse response = service.getReportById(1L);
        assertEquals(1L, response.id());
    }

    @Test
    void getAssignedReports_returnsSupervisorReports() {
        when(reportRepository.findByAssignedToUserIdAndStatusIn(eq(50L), anyList())).thenReturn(new java.util.ArrayList<>(List.of(report(1L, 10L, 1))));
        when(reportRepository.findTop30ByAssignedToUserIdAndStatusOrderByCreatedAtDesc(eq(50L), any(com.reportatucalle.modules.report.domain.entity.ReportStatus.class))).thenReturn(new java.util.ArrayList<>());
        List<ReportResponse> result = service.getAssignedReports();
        assertEquals(1, result.size());
    }

    @Test
    void updateReportStatus_updatesAndReturnsReport() {
        Report r = report(1L, 10L, 1);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        UpdateReportStatusRequest request = new UpdateReportStatusRequest("IN_PROGRESS", null);
        ReportResponse response = service.updateReportStatus(1L, request);

        assertEquals("IN_PROGRESS", response.status());
        verify(notificationPort).notifyReportStatusUpdated(any(Report.class));
    }

    @Test
    void assignReport_updatesAssigneeAndStatus() {
        Report r = report(1L, 10L, 1);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(r));
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArgument(0));

        AssignReportRequest req = new AssignReportRequest(50L);
        ReportResponse response = service.assignReport(1L, req);

        assertEquals("ASSIGNED", response.status());
        assertEquals(50L, response.assignedToUserId());
        verify(notificationPort).notifyReportAssigned(any(Report.class));
    }

    private CreateReportRequest request() {
        return new CreateReportRequest(2L, "Bache", "Hay un hueco", -12.0, -77.0, "foto.jpg");
    }

    private Report report(Long id, Long citizenId, int count) {
        return Report.builder().id(id).citizenId(citizenId).categoryId(2L)
                .title("Bache").description("Hay un hueco").imageUrl("foto.jpg")
                .location(geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(-77.0, -12.0)))
                .status(com.reportatucalle.modules.report.domain.entity.ReportStatusFactory.fromString("PENDING")).reportCount(count).build();
    }
}
