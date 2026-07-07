package com.reportatucalle.modules.report.domain.repository;

import com.reportatucalle.modules.report.domain.entity.ReportEndorsement;

import java.util.List;

public interface ReportEndorsementRepository {
    ReportEndorsement save(ReportEndorsement endorsement);
    boolean existsByReportIdAndCitizenId(Long reportId, Long citizenId);
    List<ReportEndorsement> findByReportId(Long reportId);
}