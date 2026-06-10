package com.reportatucalle.modules.optimization.application.service;

import com.reportatucalle.modules.category.application.api.CategoryFacade;
import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.optimization.domain.portsout.ConnectivityOptimizationPort;
import com.reportatucalle.modules.optimization.domain.portsout.FlowOptimizationPort;
import com.reportatucalle.modules.optimization.domain.portsout.RouteOptimizationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Optimización de Rutas.
 *
 * Responsabilidades:
 * - Orquestar la lógica de optimización de rutas para reportes
 * - Usar el CategoryFacade para obtener el tipo de algoritmo
 * - Decidir qué estrategia de optimización aplicar
 * - Delegación a adaptadores especializados por algoritmo
 *
 * Arquitectura:
 * - NO depende directamente de CategoryRepository
 * - Depende del CategoryFacade (contrato público del módulo category)
 * - Implementa el patrón "Strategy" basado en AlgorithmType
 *
 * Ventajas del Desacoplamiento:
 * - Si category cambia su implementación, esto no se ve afectado
 * - Pruebas unitarias más fáciles (mock del facade)
 * - Inversión de dependencias clara (depende de abstracciones, no implementaciones)
 */
@Service
@RequiredArgsConstructor
public class RouteOptimizationService {

    private final CategoryFacade categoryFacade;
    private final RouteOptimizationPort routeOptimizationPort;
    private final FlowOptimizationPort flowOptimizationPort;
    private final ConnectivityOptimizationPort connectivityOptimizationPort;

    /**
     * Calcula la ruta optimizada para reportes de una categoría específica.
     *
     * Flujo:
     * 1. Obtiene el tipo de algoritmo requerido para la categoría
     * 2. Valida que exista y sea aplicable
     * 3. Ejecuta la estrategia correspondiente
     * 4. Retorna la ruta optimizada
     *
     * @param categoryId el ID de la categoría
     * @param startPoint punto de inicio de la ruta
     * @param destinations puntos de destino a visitar
     * @return la ruta optimizada, o empty si la categoría no requiere optimización
     * @throws IllegalArgumentException si el tipo de algoritmo no es válido
     */
    public Optional<OptimizedRoute> optimizeRoute(Long categoryId, Coordinate startPoint, List<Coordinate> destinations) {

        // Obtener el tipo de algoritmo requerido a través del facade
        Optional<AlgorithmType> algorithmType = categoryFacade.getAlgorithmTypeForCategory(categoryId);

        if (algorithmType.isEmpty()) {
            throw new IllegalArgumentException("Categoría no encontrada: " + categoryId);
        }

        // Decidir estrategia basada en el tipo de algoritmo
        return executeStrategy(algorithmType.get(), startPoint, destinations);
    }

    /**
     * Ejecuta la estrategia de optimización correspondiente al tipo de algoritmo.
     *
     * Patrón Strategy implementado mediante Switch en AlgorithmType.
     *
     * @param algorithmType el tipo de algoritmo a usar
     * @param startPoint punto de inicio
     * @param destinations puntos destino
     * @return la ruta optimizada, o empty si el algoritmo no aplica
     */
    private Optional<OptimizedRoute> executeStrategy(AlgorithmType algorithmType,
                                                     Coordinate startPoint,
                                                     List<Coordinate> destinations) {

        return switch (algorithmType) {

            // TSP/VRP: Enrutamiento para maximizar eficiencia (baches, basura, etc.)
            case ROUTING -> {
                OptimizedRoute route = routeOptimizationPort.calculateOptimalRoute(startPoint, destinations);
                yield Optional.of(route);
            }

            // Flujo Máximo: Para problemas de distribución (fugas de agua, etc.)
            // El último destino actúa como sumidero (sink) de la red
            case FLOW -> {
                Coordinate sink = destinations.get(destinations.size() - 1);
                List<Coordinate> network = destinations.subList(0, destinations.size() - 1);
                OptimizedRoute route = flowOptimizationPort.calculateMaxFlow(startPoint, sink, network);
                yield Optional.of(route);
            }

            // Árbol de Expansión Mínima: Conectividad (semáforos, postes, etc.)
            // Se incluye el startPoint como nodo más de la red a conectar
            case CONNECTIVITY -> {
                List<Coordinate> allNodes = new ArrayList<>();
                allNodes.add(startPoint);
                allNodes.addAll(destinations);
                OptimizedRoute route = connectivityOptimizationPort.calculateMinimumSpanningTree(allNodes);
                yield Optional.of(route);
            }

            // Sin algoritmo: Solo visualización, sin optimización
            case NONE -> Optional.empty();
        };
    }

    /**
     * Verifica si una categoría requiere optimización de rutas.
     *
     * @param categoryId el ID de la categoría
     * @return true si requiere optimización, false en caso contrario
     */
    public boolean requiresRouteOptimization(Long categoryId) {
        Optional<AlgorithmType> algorithmType = categoryFacade.getAlgorithmTypeForCategory(categoryId);

        return algorithmType
                .map(type -> type != AlgorithmType.NONE)
                .orElse(false);
    }
}