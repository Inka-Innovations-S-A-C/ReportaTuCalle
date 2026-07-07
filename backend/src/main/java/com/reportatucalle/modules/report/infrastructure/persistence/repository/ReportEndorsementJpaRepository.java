package com.reportatucalle.modules.report.infrastructure.persistence.repository;

import com.reportatucalle.modules.report.infrastructure.persistence.entity.ReportEndorsementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportEndorsementJpaRepository extends JpaRepository<ReportEndorsementJpaEntity, Long> {
    boolean existsByReportIdAndCitizenId(Long reportId, Long citizenId);
    List<ReportEndorsementJpaEntity> findByReportId(Long reportId);
}