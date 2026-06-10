package com.reportatucalle.modules.report.infrastructure.persistence.adapter;

import com.reportatucalle.modules.report.domain.entity.ReportEndorsement;
import com.reportatucalle.modules.report.domain.repository.ReportEndorsementRepository;
import com.reportatucalle.modules.report.infrastructure.persistence.entity.ReportEndorsementJpaEntity;
import com.reportatucalle.modules.report.infrastructure.persistence.repository.ReportEndorsementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportEndorsementPersistenceAdapter implements ReportEndorsementRepository {

    private final ReportEndorsementJpaRepository jpaRepository;

    @Override
    public ReportEndorsement save(ReportEndorsement endorsement) {
        ReportEndorsementJpaEntity jpaEntity = ReportEndorsementJpaEntity.builder()
                .reportId(endorsement.getReportId())
                .citizenId(endorsement.getCitizenId())
                .build();
        ReportEndorsementJpaEntity saved = jpaRepository.save(jpaEntity);
        return ReportEndorsement.builder()
                .id(saved.getId())
                .reportId(saved.getReportId())
                .citizenId(saved.getCitizenId())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public boolean existsByReportIdAndCitizenId(Long reportId, Long citizenId) {
        return jpaRepository.existsByReportIdAndCitizenId(reportId, citizenId);
    }
}