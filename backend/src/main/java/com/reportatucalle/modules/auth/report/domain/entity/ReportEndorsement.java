package com.reportatucalle.modules.report.domain.entity;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReportEndorsement {

    private final Long id;
    private final Long reportId;
    private final Long citizenId;
    private final LocalDateTime createdAt;
}