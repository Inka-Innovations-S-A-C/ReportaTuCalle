package com.reportatucalle.modules.optimization.domain.models;

import java.util.List;
import java.util.Map;

public record OptimizedRoute(
    List<Coordinate> orderedStops,
    double totalDistanceKm,
    Map<String, Object> metadata
){}