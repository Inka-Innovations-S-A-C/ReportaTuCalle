package com.reportatucalle.modules.report.application.dto;

import java.time.LocalDateTime;

public record ReportResponse(
        Long id,
        String title,
        String description,
        String imageUrl,
        String status,
        Integer reportCount, // <-- Ahora el mapa sabrá cuánta gente respalda este punto
        Double latitude,
        Double longitude,
        LocalDateTime createdAt
) {}