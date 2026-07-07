package com.reportatucalle.modules.report.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateReportStatusRequest(
        @NotBlank(message = "El estado no puede estar vacío")
        String status,
        
        String resolutionImageUrl
) {}
