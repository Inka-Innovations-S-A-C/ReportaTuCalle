package com.reportatucalle.modules.optimization.domain.models;

import java.util.List;

public record OptimizedRoute(
    List<Coordinate> orderedStops,
    double totalDistanceKm
){}