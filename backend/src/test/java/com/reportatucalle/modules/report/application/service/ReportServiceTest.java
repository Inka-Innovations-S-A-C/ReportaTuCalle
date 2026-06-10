package com.reportatucalle.modules.report.application.service;

import com.reportatucalle.modules.auth.domain.entity.Role;
import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import com.reportatucalle.modules.optimization.application.service.RouteOptimizationService;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.mapper.ReportMapper;
import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportEndorsement;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import com.reportatucalle.modules.report.domain.repository.ReportEndorsementRepository;
import com.reportatucalle.modules.report.domain.repository.ReportRepository;
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
    private ReportService service;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @BeforeEach
    void setUp() {
        reportRepository = mock(ReportRepository.class);
        endorsementRepository = mock(ReportEndorsementRepository.class);
        userProfileRepository = mock(UserProfileRepository.class);
        routeOptimizationService = mock(RouteOptimizationService.class);
        service = new ReportService(reportRepository, endorsementRepository, userProfileRepository,
                new ReportMapper(), routeOptimizationService);
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
    void getOptimizedRouteForNearbyReports_whenNoReports_returnsEmpty() {
        when(reportRepository.findReportsWithinRadius(any(), eq(100.0))).thenReturn(List.of());

        assertTrue(service.getOptimizedRouteForNearbyReports(2L, -12.0, -77.0, 100.0).isEmpty());
    }

    @Test
    void getOptimizedRouteForNearbyReports_delegatesToOptimizationService() {
        Report r = report(1L, 50L, 1);
        OptimizedRoute optimized = new OptimizedRoute(List.of(new Coordinate(1L, -12.0, -77.0)), 0.5);
        when(reportRepository.findReportsWithinRadius(any(), eq(100.0))).thenReturn(List.of(r));
        when(routeOptimizationService.optimizeRoute(eq(2L), any(Coordinate.class), anyList())).thenReturn(Optional.of(optimized));

        Optional<OptimizedRoute> result = service.getOptimizedRouteForNearbyReports(2L, -12.0, -77.0, 100.0);

        assertEquals(Optional.of(optimized), result);
    }

    private CreateReportRequest request() {
        return new CreateReportRequest(2L, "Bache", "Hay un hueco", -12.0, -77.0, "foto.jpg");
    }

    private Report report(Long id, Long citizenId, int count) {
        return Report.builder().id(id).citizenId(citizenId).categoryId(2L)
                .title("Bache").description("Hay un hueco").imageUrl("foto.jpg")
                .location(geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(-77.0, -12.0)))
                .status(ReportStatus.PENDING).reportCount(count).build();
    }
}
