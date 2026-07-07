package com.reportatucalle.modules.optimization.application.dto;

import java.time.LocalDateTime;

public record RouteHistoryResponse(
        Long id,
        Long supervisorId,
        Long categoryId,
        String routeDataJson,
        Double totalDistanceKm,
        String status,
        LocalDateTime createdAt
) {}
