package com.reportatucalle.modules.report.application.service;

import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.report.application.dto.AssignReportRequest;
import com.reportatucalle.modules.report.application.dto.CreateReportRequest;
import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.dto.UpdateReportStatusRequest;
import com.reportatucalle.modules.report.application.dto.UpdateReportRequest;

import java.util.List;
import java.util.Optional;

public abstract class ReportServiceDecorator implements IReportService {
    protected final IReportService wrapped;

    public ReportServiceDecorator(IReportService wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ReportResponse createReport(CreateReportRequest request) {
        return wrapped.createReport(request);
    }

    @Override
    public ReportResponse updateReport(Long id, UpdateReportRequest request) {
        return wrapped.updateReport(id, request);
    }

    @Override
    public void deleteReport(Long id) {
        wrapped.deleteReport(id);
    }

    @Override
    public List<ReportResponse> getReportsNearby(Double latitude, Double longitude, Double radiusInMeters) {
        return wrapped.getReportsNearby(latitude, longitude, radiusInMeters);
    }

    @Override
    public Optional<OptimizedRoute> getOptimizedRouteForSelectedReports(Long categoryId, Double startLatitude, Double startLongitude, List<Long> reportIds) {
        return wrapped.getOptimizedRouteForSelectedReports(categoryId, startLatitude, startLongitude, reportIds);
    }

    @Override
    public List<ReportResponse> getAllReports() {
        return wrapped.getAllReports();
    }

    @Override
    public ReportResponse getReportById(Long id) {
        return wrapped.getReportById(id);
    }

    @Override
    public List<ReportResponse> getAssignedReports() {
        return wrapped.getAssignedReports();
    }

    @Override
    public ReportResponse updateReportStatus(Long id, UpdateReportStatusRequest request) {
        return wrapped.updateReportStatus(id, request);
    }

    @Override
    public ReportResponse assignReport(Long id, AssignReportRequest request) {
        return wrapped.assignReport(id, request);
    }

    @Override
    public ReportResponse selfAssignReport(Long id) {
        return wrapped.selfAssignReport(id);
    }

    @Override
    public List<ReportResponse> bulkSelfAssignReports(List<Long> reportIds) {
        return wrapped.bulkSelfAssignReports(reportIds);
    }

    @Override
    public ReportResponse endorseReport(Long reportId) {
        return wrapped.endorseReport(reportId);
    }
}
