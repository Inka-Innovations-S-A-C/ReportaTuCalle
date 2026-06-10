package com.reportatucalle.modules.report.application.mapper;

import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportStatus;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReportMapperTest {

    private final ReportMapper mapper = new ReportMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Test
    void toEntity_createsReportWithGpsCoordinatesInCorrectOrder() {
        CreateReportRequest request = new CreateReportRequest(
                8L, "Bache grande", "Hay un hueco", -12.05, -77.03, "foto.jpg");

        Report report = mapper.toEntity(request, 3L);

        assertEquals(3L, report.getCitizenId());
        assertEquals(8L, report.getCategoryId());
        assertEquals("Bache grande", report.getTitle());
        assertEquals(-77.03, report.getLocation().getX(), 0.000001);
        assertEquals(-12.05, report.getLocation().getY(), 0.000001);
        assertEquals(ReportStatus.PENDING, report.getStatus());
        assertEquals(1, report.getReportCount());
    }

    @Test
    void toResponse_mapsDomainReportToDto() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 9, 10, 0);
        Report report = Report.builder()
                .id(15L).citizenId(1L).categoryId(2L)
                .title("Poste caído").description("Peligroso").imageUrl("img.png")
                .location(geometryFactory.createPoint(new Coordinate(-77.01, -12.01)))
                .status(ReportStatus.IN_PROGRESS).reportCount(4).createdAt(createdAt).build();

        ReportResponse response = mapper.toResponse(report);

        assertEquals(15L, response.id());
        assertEquals("Poste caído", response.title());
        assertEquals("IN_PROGRESS", response.status());
        assertEquals(4, response.reportCount());
        assertEquals(-12.01, response.latitude(), 0.000001);
        assertEquals(-77.01, response.longitude(), 0.000001);
        assertEquals(createdAt, response.createdAt());
    }
}
