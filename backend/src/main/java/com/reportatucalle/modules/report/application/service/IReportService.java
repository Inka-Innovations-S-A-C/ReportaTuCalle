package com.reportatucalle.modules.report.application.service;

import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.report.application.dto.AssignReportRequest;
import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.dto.UpdateReportStatusRequest;
import com.reportatucalle.modules.report.application.dto.UpdateReportRequest;

import java.util.List;
import java.util.Optional;

public interface IReportService {
    ReportResponse createReport(CreateReportRequest request);
    ReportResponse updateReport(Long id, UpdateReportRequest request);
    void deleteReport(Long id);
    List<ReportResponse> getReportsNearby(Double latitude, Double longitude, Double radiusInMeters);
    Optional<OptimizedRoute> getOptimizedRouteForSelectedReports(Long categoryId, Double startLatitude, Double startLongitude, List<Long> reportIds);
    List<ReportResponse> getAllReports();
    ReportResponse getReportById(Long id);
    List<ReportResponse> getAssignedReports();
    ReportResponse updateReportStatus(Long id, UpdateReportStatusRequest request);
    ReportResponse assignReport(Long id, AssignReportRequest request);
    ReportResponse selfAssignReport(Long id);
    List<ReportResponse> bulkSelfAssignReports(List<Long> reportIds);
    ReportResponse endorseReport(Long reportId);
}
