package com.reportatucalle.modules.report.application.dto;

public record UpdateReportRequest(
        Long categoryId,
        String title,
        String description
) {}
