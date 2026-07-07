package com.reportatucalle.modules.optimization.application.service;

import com.reportatucalle.modules.category.application.api.CategoryFacade;
import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.optimization.application.dto.RouteHistoryResponse;
import com.reportatucalle.modules.optimization.application.dto.SaveRouteRequest;
import com.reportatucalle.modules.optimization.application.dto.UpdateRouteStatusRequest;
import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.optimization.domain.models.RouteHistory;
import com.reportatucalle.modules.optimization.domain.models.RouteHistoryStatus;
import com.reportatucalle.modules.optimization.domain.portsout.RouteHistoryRepository;
import com.reportatucalle.modules.optimization.application.strategy.OptimizationStrategy;
import com.reportatucalle.shared.exception.BusinessException;
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
    private final List<OptimizationStrategy> optimizationStrategies;
    private final RouteHistoryRepository routeHistoryRepository;

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
        if (algorithmType == AlgorithmType.NONE) {
            return Optional.empty();
        }

        return optimizationStrategies.stream()
                .filter(strategy -> strategy.supports(algorithmType))
                .findFirst()
                .flatMap(strategy -> strategy.optimize(startPoint, destinations));
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

    public RouteHistoryResponse saveRoute(SaveRouteRequest request) {
        RouteHistory history = RouteHistory.builder()
                .supervisorId(request.supervisorId())
                .categoryId(request.categoryId())
                .routeDataJson(request.routeDataJson())
                .totalDistanceKm(request.totalDistanceKm())
                .status(RouteHistoryStatus.SAVED)
                .build();
                
        RouteHistory saved = routeHistoryRepository.save(history);
        return toResponse(saved);
    }

    public List<RouteHistoryResponse> getRoutesBySupervisorId(Long supervisorId) {
        return routeHistoryRepository.findBySupervisorId(supervisorId).stream()
                .map(this::toResponse)
                .toList();
    }

    public RouteHistoryResponse updateRouteStatus(Long id, UpdateRouteStatusRequest request) {
        RouteHistory existing = routeHistoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Ruta no encontrada", "ROUTE_NOT_FOUND"));
                
        RouteHistory updated = RouteHistory.builder()
                .id(existing.getId())
                .supervisorId(existing.getSupervisorId())
                .categoryId(existing.getCategoryId())
                .routeDataJson(existing.getRouteDataJson())
                .totalDistanceKm(existing.getTotalDistanceKm())
                .status(RouteHistoryStatus.valueOf(request.status()))
                .createdAt(existing.getCreatedAt())
                .build();
                
        RouteHistory saved = routeHistoryRepository.save(updated);
        return toResponse(saved);
    }

    private RouteHistoryResponse toResponse(RouteHistory history) {
        return new RouteHistoryResponse(
                history.getId(),
                history.getSupervisorId(),
                history.getCategoryId(),
                history.getRouteDataJson(),
                history.getTotalDistanceKm(),
                history.getStatus().name(),
                history.getCreatedAt()
        );
    }
}