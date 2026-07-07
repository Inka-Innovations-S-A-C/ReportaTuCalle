package com.reportatucalle.modules.report.application.service;

import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.report.application.dto.AssignReportRequest;
import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.dto.UpdateReportRequest;
import com.reportatucalle.modules.report.application.dto.UpdateReportStatusRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy Pattern for caching IReportService requests
 */
@Service
@Primary
public class ReportCacheProxy implements IReportService {

    private final IReportService realService;
    private final Map<Long, ReportResponse> cache = new ConcurrentHashMap<>();

    // Qualify the real service so Spring injects ReportService, not this Proxy
    public ReportCacheProxy(@Qualifier("reportService") IReportService realService) {
        this.realService = realService;
    }

    @Override
    public ReportResponse getReportById(Long id) {
        return cache.computeIfAbsent(id, realService::getReportById);
    }

    @Override
    public ReportResponse createReport(CreateReportRequest request) {
        ReportResponse response = realService.createReport(request);
        cache.put(response.id(), response);
        return response;
    }

    @Override
    public ReportResponse updateReport(Long id, UpdateReportRequest request) {
        ReportResponse response = realService.updateReport(id, request);
        cache.put(id, response);
        return response;
    }

    @Override
    public void deleteReport(Long id) {
        realService.deleteReport(id);
        cache.remove(id);
    }

    @Override
    public List<ReportResponse> getReportsNearby(Double latitude, Double longitude, Double radiusInMeters) {
        return realService.getReportsNearby(latitude, longitude, radiusInMeters);
    }

    @Override
    public Optional<OptimizedRoute> getOptimizedRouteForSelectedReports(Long categoryId, Double startLatitude, Double startLongitude, List<Long> reportIds) {
        // En un caso real podríamos cachear esto también si los parámetros son idénticos
        return realService.getOptimizedRouteForSelectedReports(categoryId, startLatitude, startLongitude, reportIds);
    }

    @Override
    public List<ReportResponse> getAllReports() {
        return realService.getAllReports();
    }

    @Override
    public List<ReportResponse> getAssignedReports() {
        return realService.getAssignedReports();
    }

    @Override
    public ReportResponse updateReportStatus(Long id, UpdateReportStatusRequest request) {
        ReportResponse response = realService.updateReportStatus(id, request);
        cache.put(id, response);
        return response;
    }

    @Override
    public ReportResponse assignReport(Long id, AssignReportRequest request) {
        ReportResponse response = realService.assignReport(id, request);
        cache.put(id, response);
        return response;
    }

    @Override
    public ReportResponse selfAssignReport(Long id) {
        ReportResponse response = realService.selfAssignReport(id);
        cache.put(id, response);
        return response;
    }

    @Override
    public List<ReportResponse> bulkSelfAssignReports(List<Long> reportIds) {
        List<ReportResponse> responses = realService.bulkSelfAssignReports(reportIds);
        for (ReportResponse r : responses) {
            cache.put(r.id(), r);
        }
        return responses;
    }

    @Override
    public ReportResponse endorseReport(Long reportId) {
        ReportResponse response = realService.endorseReport(reportId);
        cache.put(reportId, response);
        return response;
    }
}
