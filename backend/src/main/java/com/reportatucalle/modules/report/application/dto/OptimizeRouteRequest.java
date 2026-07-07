package com.reportatucalle.modules.report.application.dto;

import java.util.List;

public record OptimizeRouteRequest(
        Long categoryId,
        Double startLatitude,
        Double startLongitude,
        List<Long> reportIds
) {}
