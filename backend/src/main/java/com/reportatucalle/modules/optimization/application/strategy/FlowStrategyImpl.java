package com.reportatucalle.modules.optimization.application.strategy;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.optimization.domain.portsout.FlowOptimizationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FlowStrategyImpl implements OptimizationStrategy {

    private final FlowOptimizationPort flowOptimizationPort;

    @Override
    public boolean supports(AlgorithmType type) {
        return type == AlgorithmType.FLOW;
    }

    @Override
    public Optional<OptimizedRoute> optimize(Coordinate startPoint, List<Coordinate> destinations) {
        if (destinations.size() < 2) {
            return Optional.empty();
        }
        // Para tuberías, el inicio del flujo (source) es el primer reporte, no la ubicación del humano
        Coordinate source = destinations.get(0);
        Coordinate sink = destinations.get(destinations.size() - 1);
        List<Coordinate> network = destinations.subList(1, destinations.size() - 1);
        return Optional.of(flowOptimizationPort.calculateMaxFlow(source, sink, network));
    }
}
