package com.reportatucalle.modules.optimization.application.strategy;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.optimization.domain.portsout.ConnectivityOptimizationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConnectivityStrategyImpl implements OptimizationStrategy {

    private final ConnectivityOptimizationPort connectivityOptimizationPort;

    @Override
    public boolean supports(AlgorithmType type) {
        return type == AlgorithmType.CONNECTIVITY;
    }

    @Override
    public Optional<OptimizedRoute> optimize(Coordinate startPoint, List<Coordinate> destinations) {
        // Para redes físicas (cables), la ubicación del humano (startPoint) no forma parte de la red
        List<Coordinate> allNodes = new ArrayList<>(destinations);
        return Optional.of(connectivityOptimizationPort.calculateMinimumSpanningTree(allNodes));
    }
}
