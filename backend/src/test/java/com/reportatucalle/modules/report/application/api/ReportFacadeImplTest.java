package com.reportatucalle.modules.report.application.api;

import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import com.reportatucalle.modules.report.domain.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReportFacadeImplTest {

    private final ReportRepository repository = mock(ReportRepository.class);
    private final ReportFacadeImpl facade = new ReportFacadeImpl(repository);
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Test
    void facadeReportsBasicQueries() {
        Report r1 = report(1L, 10L, 2L);
        Report r2 = report(2L, 10L, 2L);
        when(repository.findById(1L)).thenReturn(Optional.of(r1));
        when(repository.findByCitizenId(10L)).thenReturn(List.of(r1, r2));
        when(repository.findReportsWithinRadius(any(), eq(50.0))).thenReturn(List.of(r1, r2));

        assertEquals(Optional.of(1L), facade.getReportIdIfExists(1L));
        assertEquals(2, facade.countReportsByCitizen(10L));
        assertEquals(0, facade.countReportsByCategory(2L));
        assertFalse(facade.hasCategoryReports(2L));
        assertEquals(List.of(1L, 2L), facade.getReportIdsNearby(-12, -77, 50.0));
    }

    @Test
    void facadeHandlesMissingReport() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertEquals(Optional.empty(), facade.getReportIdIfExists(99L));
    }

    private Report report(Long id, Long citizenId, Long categoryId) {
        return Report.builder().id(id).citizenId(citizenId).categoryId(categoryId)
                .title("T").description("D")
                .location(geometryFactory.createPoint(new Coordinate(-77, -12)))
                .status(ReportStatus.PENDING).build();
    }
}
