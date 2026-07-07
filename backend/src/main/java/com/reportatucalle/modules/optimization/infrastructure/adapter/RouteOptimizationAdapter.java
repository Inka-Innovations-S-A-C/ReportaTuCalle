package com.reportatucalle.modules.optimization.infrastructure.adapter;

import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.optimization.domain.portsout.RouteOptimizationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador de Optimización de Rutas.
 * 
 * Implementa el puerto de salida RouteOptimizationPort.
 * 
 * Responsabilidades:
 * - Implementar el algoritmo TSP/VRP
 * - Calcular la ruta optimizada
 * - Retornar resultados en el formato esperado por el dominio
 * 
 * Notas:
 * - Actualmente implementa un algoritmo simple (nearest neighbor)
 * - Puede reemplazarse con bibliotecas especializadas (JSPRIT, OR-Tools, etc.)
 * - El dominio NO conoce estos detalles de implementación
 */
@Component
@RequiredArgsConstructor
public class RouteOptimizationAdapter implements RouteOptimizationPort {
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    /**
     * Calcula la ruta óptima usando el algoritmo de Vecino Más Cercano (Nearest Neighbor).
     * 
     * Este es un algoritmo heurístico simple O(n²) que:
     * 1. Comienza en el punto de inicio
     * 2. Siempre va al destino no visitado más cercano
     * 3. Repite hasta visitar todos
     * 
     * Nota: Para problemas complejos, reemplazar con JSPRIT o OR-Tools.
     * 
     * @param startPoint punto de inicio
     * @param destinations lista de destinos a visitar
     * @return ruta optimizada con distancia total
     */
    @Override
    public OptimizedRoute calculateOptimalRoute(Coordinate startPoint, List<Coordinate> destinations) {
        if (destinations.isEmpty()) {
            return new OptimizedRoute(List.of(startPoint), 0.0, java.util.Map.of());
        }
        
        List<Coordinate> orderedStops = new ArrayList<>();
        orderedStops.add(startPoint);
        
        // Implementación del algoritmo Nearest Neighbor
        List<Coordinate> remaining = new ArrayList<>(destinations);
        Coordinate current = startPoint;
        double totalDistance = 0.0;
        
        while (!remaining.isEmpty()) {
            // Encontrar el destino más óptimo (TSP Ponderado por Impacto Ciudadano / Endorsements)
            Coordinate bestNext = remaining.get(0);
            double bestHeuristic = Double.MAX_VALUE;
            double actualDistanceToBest = 0.0;
            int bestIndex = 0;
            
            for (int i = 0; i < remaining.size(); i++) {
                Coordinate candidate = remaining.get(i);
                double distance = calculateDistance(current, candidate);
                
                // Fórmula de la tesis: 
                // A mayor cantidad de endorsements (reportCount), el "costo" matemático se reduce.
                // Esto engaña al TSP haciéndole creer que los reportes más votados están "más cerca",
                // priorizando su atención.
                double heuristicCost = distance / (1.0 + (candidate.reportCount() * 0.5));
                
                if (heuristicCost < bestHeuristic) {
                    bestHeuristic = heuristicCost;
                    bestNext = candidate;
                    actualDistanceToBest = distance;
                    bestIndex = i;
                }
            }
            
            // Agregar el mejor a la ruta
            orderedStops.add(bestNext);
            totalDistance += actualDistanceToBest;
            
            // Remover de los destinos pendientes
            remaining.remove(bestIndex);
            current = bestNext;
        }
        
        return new OptimizedRoute(orderedStops, totalDistance, java.util.Map.of());
    }
    
    /**
     * Calcula la distancia geométrica entre dos coordenadas usando la Fórmula de Haversine.
     * * Esta fórmula asume una Tierra esférica perfecta, lo cual es ideal para la mayoría de 
     * las aplicaciones de geolocalización urbana (margen de error típico < 0.5%).
     * * Para rutas viales reales con tráfico o restricciones de calles, considerar 
     * la integración con un motor de ruteo como OSRM o Google Maps API.
     * @param from punto de origen
     * @param to punto de destino
     * @return distancia en kilómetros
     */
    private double calculateDistance(Coordinate from, Coordinate to) {
        
        double lat1 = Math.toRadians(from.latitude());
        double lat2 = Math.toRadians(to.latitude());
        double deltaLat = Math.toRadians(to.latitude() - from.latitude());
        double deltaLon = Math.toRadians(to.longitude() - from.longitude());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                   + Math.cos(lat1) * Math.cos(lat2)
                   * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
