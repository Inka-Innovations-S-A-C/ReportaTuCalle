package com.reportatucalle.modules.report.application.mapper;

import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.domain.entity.Report;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    // Instancia el creador de geometrías indicando que usamos SRID 4326 (GPS Mundial)
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * Convierte la petición del ciudadano en una entidad lista para PostGIS.
     */
    public Report toEntity(CreateReportRequest request, Long citizenId) {
        
        // Cuidado con el orden, Coordinate en JTS exige (X, Y) -> (Longitud, Latitud)
        Coordinate coordinate = new Coordinate(request.longitude(), request.latitude());
        Point location = geometryFactory.createPoint(coordinate);

        return Report.builder()
                .citizenId(citizenId)
                .categoryId(request.categoryId())
                .title(request.title())
                .description(request.description())
                .imageUrl(request.imageUrl())
                .location(location)
                .build();
    }

    /**
     * Convierte la entidad de base de datos en un DTO para el Frontend.
     */
    public ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getCitizenId(),
                report.getCategoryId(),
                report.getAssignedToUserId(),
                report.getTitle(),
                report.getDescription(),
                report.getImageUrl(),
                report.getResolutionImageUrl(),
                report.getStatus().getName(),
                report.getReportCount(), // Mapeo del contador consolidado
                report.getLocation().getY(), 
                report.getLocation().getX(), 
                report.getCreatedAt()
        );
    }
}