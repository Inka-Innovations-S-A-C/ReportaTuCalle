package com.reportatucalle.service;

import com.reportatucalle.dto.CategoriaResponse;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CategoriaService {
    
    private final Map<Long, CategoriaResponse> categorias = new HashMap<>();
    
    public CategoriaService() {
        // Cargar categorías por defecto
        categorias.put(1L, new CategoriaResponse(1L, "Baches", "Daños en el pavimento", "icon-pothole", "#FF0000"));
        categorias.put(2L, new CategoriaResponse(2L, "Fuga de agua", "Pérdida de agua en tuberías", "icon-water", "#0099FF"));
        categorias.put(3L, new CategoriaResponse(3L, "Falta de iluminación", "Alumbrado público deficiente", "icon-light", "#FFFF00"));
        categorias.put(4L, new CategoriaResponse(4L, "Alcantarilla tapada", "Sistema de drenaje obstruido", "icon-drain", "#8B4513"));
        categorias.put(5L, new CategoriaResponse(5L, "Árbol caído", "Árbol bloqueando la vía", "icon-tree", "#228B22"));
        categorias.put(6L, new CategoriaResponse(6L, "Basura acumulada", "Recolección deficiente", "icon-trash", "#A9A9A9"));
        categorias.put(7L, new CategoriaResponse(7L, "Señal dañada", "Señalización vial defectuosa", "icon-sign", "#FFA500"));
        categorias.put(8L, new CategoriaResponse(8L, "Otro", "Problema no clasificado", "icon-question", "#808080"));
    }
    
    public List<CategoriaResponse> listar() {
        return new ArrayList<>(categorias.values());
    }
    
    public CategoriaResponse obtener(Long id) {
        CategoriaResponse categoria = categorias.get(id);
        if (categoria == null) {
            throw new NoSuchElementException("Categoría no encontrada");
        }
        return categoria;
    }
}
