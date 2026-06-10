package com.reportatucalle.modules.optimization.domain.portsout;

import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import java.util.List;

public interface FlowOptimizationPort {
    OptimizedRoute calculateMaxFlow(Coordinate source, Coordinate sink, List<Coordinate> network);
}