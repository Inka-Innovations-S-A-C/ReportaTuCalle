package com.reportatucalle.service;

import com.reportatucalle.dto.ReporteRequest;
import com.reportatucalle.dto.ReporteResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReporteService {
    
    private final Map<Long, ReporteResponse> reportes = new HashMap<>();
    private Long nextId = 1L;
    
    public ReporteResponse crear(ReporteRequest request) {
        // Validaciones
        if (request.getTitulo() == null || request.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Título es requerido");
        }
        
        if (request.getDescripcion() == null || request.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("Descripción es requerida");
        }
        
        if (request.getLatitud() == null || request.getLongitud() == null) {
            throw new IllegalArgumentException("Ubicación (latitud/longitud) es requerida");
        }
        
        if (request.getCategoriaId() == null) {
            throw new IllegalArgumentException("Categoría es requerida");
        }
        
        Long id = nextId++;
        ReporteResponse response = new ReporteResponse();
        response.setId(id);
        response.setTitulo(request.getTitulo());
        response.setDescripcion(request.getDescripcion());
        response.setLatitud(request.getLatitud());
        response.setLongitud(request.getLongitud());
        response.setCategoriaId(request.getCategoriaId());
        response.setEstado("nuevo");
        response.setPrioridad("media");
        response.setFechaCreacion(LocalDateTime.now());
        
        reportes.put(id, response);
        return response;
    }
    
    public ReporteResponse obtener(Long id) {
        ReporteResponse reporte = reportes.get(id);
        if (reporte == null) {
            throw new NoSuchElementException("Reporte no encontrado");
        }
        return reporte;
    }
    
    public List<ReporteResponse> listar() {
        return new ArrayList<>(reportes.values());
    }
    
    public List<ReporteResponse> listarPorEstado(String estado) {
        return reportes.values().stream()
            .filter(r -> r.getEstado().equals(estado))
            .toList();
    }
    
    public ReporteResponse cambiarEstado(Long id, String nuevoEstado) {
        ReporteResponse reporte = obtener(id);
        
        // Estados válidos
        List<String> estadosValidos = List.of(
            "nuevo", "revisado", "asignado", "en_proceso", "resuelto", "cerrado"
        );
        
        if (!estadosValidos.contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no válido: " + nuevoEstado);
        }
        
        reporte.setEstado(nuevoEstado);
        return reporte;
    }
    
    public void eliminar(Long id) {
        if (!reportes.containsKey(id)) {
            throw new NoSuchElementException("Reporte no encontrado");
        }
        reportes.remove(id);
    }
}
