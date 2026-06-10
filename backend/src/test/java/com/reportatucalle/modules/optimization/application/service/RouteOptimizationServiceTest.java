package com.reportatucalle.modules.optimization.application.service;

import com.reportatucalle.modules.category.application.api.CategoryFacade;
import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.optimization.domain.portsout.ConnectivityOptimizationPort;
import com.reportatucalle.modules.optimization.domain.portsout.FlowOptimizationPort;
import com.reportatucalle.modules.optimization.domain.portsout.RouteOptimizationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteOptimizationServiceTest {

    private CategoryFacade categoryFacade;
    private RouteOptimizationPort routePort;
    private FlowOptimizationPort flowPort;
    private ConnectivityOptimizationPort connectivityPort;
    private RouteOptimizationService service;

    private final Coordinate start = new Coordinate(null, -12.0, -77.0);
    private final List<Coordinate> destinations = List.of(
            new Coordinate(1L, -12.01, -77.01),
            new Coordinate(2L, -12.02, -77.02)
    );

    @BeforeEach
    void setUp() {
        categoryFacade = mock(CategoryFacade.class);
        routePort = mock(RouteOptimizationPort.class);
        flowPort = mock(FlowOptimizationPort.class);
        connectivityPort = mock(ConnectivityOptimizationPort.class);
        service = new RouteOptimizationService(categoryFacade, routePort, flowPort, connectivityPort);
    }

    @Test
    void optimizeRoute_withRouting_delegatesToRoutePort() {
        OptimizedRoute expected = new OptimizedRoute(List.of(start, destinations.get(0)), 1.5);
        when(categoryFacade.getAlgorithmTypeForCategory(1L)).thenReturn(Optional.of(AlgorithmType.ROUTING));
        when(routePort.calculateOptimalRoute(start, destinations)).thenReturn(expected);

        Optional<OptimizedRoute> result = service.optimizeRoute(1L, start, destinations);

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        verify(routePort).calculateOptimalRoute(start, destinations);
    }

    @Test
    void optimizeRoute_withFlow_usesLastDestinationAsSink() {
        OptimizedRoute expected = new OptimizedRoute(destinations, 2.0);
        when(categoryFacade.getAlgorithmTypeForCategory(2L)).thenReturn(Optional.of(AlgorithmType.FLOW));
        when(flowPort.calculateMaxFlow(start, destinations.get(1), List.of(destinations.get(0)))).thenReturn(expected);

        Optional<OptimizedRoute> result = service.optimizeRoute(2L, start, destinations);

        assertEquals(expected, result.orElseThrow());
        verify(flowPort).calculateMaxFlow(start, destinations.get(1), List.of(destinations.get(0)));
    }

    @Test
    void optimizeRoute_withConnectivity_includesStartPoint() {
        OptimizedRoute expected = new OptimizedRoute(List.of(start, destinations.get(0), destinations.get(1)), 3.0);
        when(categoryFacade.getAlgorithmTypeForCategory(3L)).thenReturn(Optional.of(AlgorithmType.CONNECTIVITY));
        when(connectivityPort.calculateMinimumSpanningTree(List.of(start, destinations.get(0), destinations.get(1))))
                .thenReturn(expected);

        Optional<OptimizedRoute> result = service.optimizeRoute(3L, start, destinations);

        assertEquals(expected, result.orElseThrow());
    }

    @Test
    void optimizeRoute_withNone_returnsEmpty() {
        when(categoryFacade.getAlgorithmTypeForCategory(4L)).thenReturn(Optional.of(AlgorithmType.NONE));

        assertTrue(service.optimizeRoute(4L, start, destinations).isEmpty());
        verifyNoInteractions(routePort, flowPort, connectivityPort);
    }

    @Test
    void optimizeRoute_whenCategoryMissing_throwsException() {
        when(categoryFacade.getAlgorithmTypeForCategory(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.optimizeRoute(99L, start, destinations));

        assertTrue(ex.getMessage().contains("Categoría no encontrada"));
    }

    @Test
    void requiresRouteOptimization_returnsExpectedBoolean() {
        when(categoryFacade.getAlgorithmTypeForCategory(1L)).thenReturn(Optional.of(AlgorithmType.ROUTING));
        when(categoryFacade.getAlgorithmTypeForCategory(2L)).thenReturn(Optional.of(AlgorithmType.NONE));
        when(categoryFacade.getAlgorithmTypeForCategory(3L)).thenReturn(Optional.empty());

        assertTrue(service.requiresRouteOptimization(1L));
        assertFalse(service.requiresRouteOptimization(2L));
        assertFalse(service.requiresRouteOptimization(3L));
    }
}
