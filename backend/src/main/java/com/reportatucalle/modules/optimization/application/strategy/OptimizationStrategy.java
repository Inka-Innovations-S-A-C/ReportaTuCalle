package com.reportatucalle.modules.optimization.application.strategy;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;

import java.util.List;
import java.util.Optional;

public interface OptimizationStrategy {
    boolean supports(AlgorithmType type);
    Optional<OptimizedRoute> optimize(Coordinate startPoint, List<Coordinate> destinations);
}
