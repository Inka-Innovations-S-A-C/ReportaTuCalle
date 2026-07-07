package com.reportatucalle.modules.optimization.domain.models;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RouteHistory {
    private final Long id;
    private final Long supervisorId;
    private final Long categoryId;
    private final String routeDataJson;
    private final Double totalDistanceKm;
    private final RouteHistoryStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
