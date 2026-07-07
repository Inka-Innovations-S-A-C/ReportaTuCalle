package com.reportatucalle.modules.report.application.dto;

import jakarta.validation.constraints.NotNull;

public record AssignReportRequest(
        Long assignedToUserId
) {}
