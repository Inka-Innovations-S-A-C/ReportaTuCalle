package com.reportatucalle.modules.report.application.api;

import com.reportatucalle.modules.report.domain.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de ReportFacade.
 *
 * Orquesta llamadas al puerto ReportRepository.
 * Expone SOLO lo que otros módulos necesitan, no detalles internos.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportFacadeImpl implements ReportFacade {

    private final ReportRepository reportRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public Optional<Long> getReportIdIfExists(Long reportId) {
        return reportRepository.findById(reportId)
                .map(report -> report.getId());
    }

    @Override
    public long countReportsByCitizen(Long citizenId) {
        return reportRepository.findByCitizenId(citizenId).size();
    }

    @Override
    public long countReportsByCategory(Long categoryId) {
        return 0L;
    }

    @Override
    public boolean hasCategoryReports(Long categoryId) {
        return countReportsByCategory(categoryId) > 0;
    }

    @Override
    public List<Long> getReportIdsNearby(double latitude, double longitude, double radiusInMeters) {
        // JTS exige (X, Y) -> (Longitud, Latitud) para el plano geoespacial
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        return reportRepository.findReportsWithinRadius(location, radiusInMeters)
                .stream()
                .map(report -> report.getId())
                .collect(Collectors.toList());
    }
}