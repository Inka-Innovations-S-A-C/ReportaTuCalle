package com.reportatucalle.modules.report.domain.repository;

import com.reportatucalle.modules.report.domain.entity.ReportEndorsement;

public interface ReportEndorsementRepository {
    ReportEndorsement save(ReportEndorsement endorsement);
    boolean existsByReportIdAndCitizenId(Long reportId, Long citizenId);
}