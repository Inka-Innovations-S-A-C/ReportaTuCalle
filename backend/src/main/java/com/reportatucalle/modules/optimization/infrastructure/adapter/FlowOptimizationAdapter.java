package com.reportatucalle.modules.optimization.infrastructure.adapter;

import com.reportatucalle.modules.optimization.domain.models.Coordinate;
import com.reportatucalle.modules.optimization.domain.models.OptimizedRoute;
import com.reportatucalle.modules.optimization.domain.portsout.FlowOptimizationPort;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Adaptador de Flujo Máximo.
 *
 * Implementa Ford-Fulkerson con BFS (Edmonds-Karp).
 * Útil para: fugas de agua, distribución de recursos en red urbana.
 *
 * El resultado relevante para el supervisor es el CAMINO por donde
 * pasa el flujo — qué puntos conectar y en qué orden para llegar a la fuga.
 */
@Component
public class FlowOptimizationAdapter implements FlowOptimizationPort {

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public OptimizedRoute calculateMaxFlow(Coordinate source, Coordinate sink, List<Coordinate> network) {
        List<Coordinate> allNodes = new ArrayList<>();
        allNodes.add(source);
        allNodes.addAll(network);
        allNodes.add(sink);

        int n = allNodes.size();
        int[][] capacity = buildCapacityMatrix(allNodes, n);
        int[][] flow = new int[n][n];

        int sourceIdx = 0;
        int sinkIdx = n - 1;

        // Ejecutar Ford-Fulkerson hasta que no haya más caminos aumentantes
        while (true) {
            int[] parent = bfs(capacity, flow, n, sourceIdx, sinkIdx);
            if (parent == null) break;

            int pathFlow = Integer.MAX_VALUE;
            int v = sinkIdx;
            while (v != sourceIdx) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, capacity[u][v] - flow[u][v]);
                v = u;
            }

            v = sinkIdx;
            while (v != sourceIdx) {
                int u = parent[v];
                flow[u][v] += pathFlow;
                flow[v][u] -= pathFlow;
                v = u;
            }
        }

        // Lo que le interesa al supervisor: el camino ordenado de coordenadas
        List<Coordinate> path = reconstructPath(flow, allNodes, sourceIdx, sinkIdx, n);
        double totalDistance = calculateTotalDistance(path);

        return new OptimizedRoute(path, totalDistance);
    }

    private int[][] buildCapacityMatrix(List<Coordinate> nodes, int n) {
        int[][] capacity = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    double dist = calculateDistance(nodes.get(i), nodes.get(j));
                    capacity[i][j] = (int) (100.0 / (dist + 0.001));
                }
            }
        }
        return capacity;
    }

    private int[] bfs(int[][] capacity, int[][] flow, int n, int source, int sink) {
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        parent[source] = source;

        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);

        while (!queue.isEmpty() && parent[sink] == -1) {
            int u = queue.poll();
            for (int v = 0; v < n; v++) {
                if (parent[v] == -1 && capacity[u][v] - flow[u][v] > 0) {
                    parent[v] = u;
                    queue.add(v);
                }
            }
        }

        return parent[sink] == -1 ? null : parent;
    }

    private List<Coordinate> reconstructPath(int[][] flow, List<Coordinate> nodes,
                                              int source, int sink, int n) {
        List<Coordinate> path = new ArrayList<>();
        boolean[] visited = new boolean[n];
        int current = source;

        path.add(nodes.get(current));
        visited[current] = true;

        while (current != sink) {
            boolean moved = false;
            for (int next = 0; next < n; next++) {
                if (!visited[next] && flow[current][next] > 0) {
                    path.add(nodes.get(next));
                    visited[next] = true;
                    current = next;
                    moved = true;
                    break;
                }
            }
            if (!moved) break;
        }

        return path;
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