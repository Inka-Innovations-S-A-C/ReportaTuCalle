package com.reportatucalle.modules.optimization.infrastructure.adapter;

import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptimizationAdaptersTest {

    private final Coordinate start = new Coordinate(null, -12.0, -77.0);
    private final Coordinate near = new Coordinate(1L, -12.001, -77.001);
    private final Coordinate far = new Coordinate(2L, -12.050, -77.050);

    @Test
    void routeOptimizationAdapter_returnsOnlyStartWhenNoDestinations() {
        RouteOptimizationAdapter adapter = new RouteOptimizationAdapter();

        OptimizedRoute route = adapter.calculateOptimalRoute(start, List.of());

        assertEquals(List.of(start), route.orderedStops());
        assertEquals(0.0, route.totalDistanceKm());
    }

    @Test
    void routeOptimizationAdapter_ordersNearestNeighborAndCalculatesDistance() {
        RouteOptimizationAdapter adapter = new RouteOptimizationAdapter();

        OptimizedRoute route = adapter.calculateOptimalRoute(start, List.of(far, near));

        assertEquals(start, route.orderedStops().get(0));
        assertEquals(near, route.orderedStops().get(1));
        assertEquals(far, route.orderedStops().get(2));
        assertTrue(route.totalDistanceKm() > 0);
    }

    @Test
    void connectivityOptimizationAdapter_handlesEmptyAndBuildsTree() {
        ConnectivityOptimizationAdapter adapter = new ConnectivityOptimizationAdapter();

        OptimizedRoute empty = adapter.calculateMinimumSpanningTree(List.of());
        OptimizedRoute route = adapter.calculateMinimumSpanningTree(List.of(start, far, near));

        assertTrue(empty.orderedStops().isEmpty());
        assertEquals(0.0, empty.totalDistanceKm());
        assertEquals(3, route.orderedStops().size());
        assertEquals(start, route.orderedStops().get(0));
        assertTrue(route.totalDistanceKm() > 0);
    }

    @Test
    void flowOptimizationAdapter_returnsPathFromSourceToSink() {
        FlowOptimizationAdapter adapter = new FlowOptimizationAdapter();

        OptimizedRoute route = adapter.calculateMaxFlow(start, far, List.of(near));

        assertFalse(route.orderedStops().isEmpty());
        assertEquals(start, route.orderedStops().get(0));
        assertTrue(route.totalDistanceKm() >= 0);
    }
}
