package com.reportatucalle.modules.optimization.application.strategy;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.optimization.domain.portsout.RouteOptimizationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoutingStrategyImpl implements OptimizationStrategy {

    private final RouteOptimizationPort routeOptimizationPort;

    @Override
    public boolean supports(AlgorithmType type) {
        return type == AlgorithmType.ROUTING;
    }

    @Override
    public Optional<OptimizedRoute> optimize(Coordinate startPoint, List<Coordinate> destinations) {
        return Optional.of(routeOptimizationPort.calculateOptimalRoute(startPoint, destinations));
    }
}
