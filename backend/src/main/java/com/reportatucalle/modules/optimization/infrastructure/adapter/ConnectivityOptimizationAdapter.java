package com.reportatucalle.modules.optimization.infrastructure.adapter;

import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.optimization.domain.portsout.ConnectivityOptimizationPort;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Adaptador de Conectividad Mínima.
 *
 * Implementa el algoritmo de Prim para Árbol de Expansión Mínima (MST).
 * Útil para: semáforos, postes de luz, tendido eléctrico urbano.
 *
 * Idea: conectar todos los puntos con la menor distancia total posible,
 * como trazar cables entre postes usando el menor cable posible.
 */
@Component
public class ConnectivityOptimizationAdapter implements ConnectivityOptimizationPort {

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public OptimizedRoute calculateMinimumSpanningTree(List<Coordinate> nodes) {
        if (nodes.isEmpty()) {
            return new OptimizedRoute(List.of(), 0.0, java.util.Map.of());
        }

        int n = nodes.size();
        double[] minEdge = new double[n];      // menor distancia al MST
        int[] parent = new int[n];             // nodo padre en el MST
        boolean[] inMST = new boolean[n];      // si ya está en el MST

        Arrays.fill(minEdge, Double.MAX_VALUE);
        Arrays.fill(parent, -1);
        minEdge[0] = 0.0; // empezar desde el primer nodo

        double totalDistance = 0.0;

        for (int iter = 0; iter < n; iter++) {
            // Encontrar el nodo con menor distancia al MST que no esté incluido
            int u = -1;
            for (int v = 0; v < n; v++) {
                if (!inMST[v] && (u == -1 || minEdge[v] < minEdge[u])) {
                    u = v;
                }
            }

            inMST[u] = true;
            totalDistance += minEdge[u];

            // Actualizar distancias de los vecinos
            for (int v = 0; v < n; v++) {
                if (!inMST[v]) {
                    double dist = calculateDistance(nodes.get(u), nodes.get(v));
                    if (dist < minEdge[v]) {
                        minEdge[v] = dist;
                        parent[v] = u;
                    }
                }
            }
        }

        // Reconstruir el recorrido del MST en orden
        List<Coordinate> path = buildOrderedPath(nodes, parent, n);
        totalDistance = calculateTotalDistance(path);

        return new OptimizedRoute(path, totalDistance, java.util.Map.of());
    }

    /**
     * Construye el recorrido del MST como lista ordenada de coordenadas.
     * Hace un recorrido DFS SOBRE EL ÁRBOL incluyendo el retroceso (backtracking),
     * para que si se dibuja o enruta secuencialmente, trace exactamente las
     * aristas del árbol sin saltos.
     */
    private List<Coordinate> buildOrderedPath(List<Coordinate> nodes, int[] parent, int n) {
        Map<Integer, List<Integer>> children = new HashMap<>();
        for (int i = 0; i < n; i++) children.put(i, new ArrayList<>());
        for (int i = 1; i < n; i++) {
            if (parent[i] != -1) children.get(parent[i]).add(i);
        }

        List<Coordinate> path = new ArrayList<>();
        dfsWalk(0, children, nodes, path);
        return path;
    }

    private void dfsWalk(int current, Map<Integer, List<Integer>> children, List<Coordinate> nodes, List<Coordinate> path) {
        path.add(nodes.get(current));
        for (int child : children.get(current)) {
            dfsWalk(child, children, nodes, path);
            // Retroceso (backtracking) para mantener la continuidad del trazo
            path.add(nodes.get(current));
        }
    }

    private double calculateTotalDistance(List<Coordinate> path) {
        double total = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += calculateDistance(path.get(i), path.get(i + 1));
        }
        return total;
    }

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