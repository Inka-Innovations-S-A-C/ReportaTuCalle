package com.reportatucalle.modules.category.infrastructure.controller;

import com.reportatucalle.modules.category.application.dto.CategoryResponse;
import com.reportatucalle.modules.category.application.dto.CreateCategoryRequest;
import com.reportatucalle.modules.category.application.service.CategoryService;
import com.reportatucalle.shared.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST para la gestión de Categorías.
 * 
 * Responsabilidades:
 * - Exponer endpoints HTTP
 * - Validar entrada (parámetros, cuerpo)
 * - Invocar servicios de aplicación
 * - Formatear respuestas
 * 
 * Arquitectura:
 * - Controller → Service (aplicación) → Repository (puerto) → Adapter (infra)
 * - El Controller SOLO habla con CategoryService, no con detalles de persistencia
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * GET /api/v1/categories
     * 
     * Endpoint público para listar categorías activas.
     * Usado por el frontend para llenar el desplegable (Select)
     * en el formulario de creación de reportes.
     * 
     * @return lista de categorías activas
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getActiveCategories() {
        List<CategoryResponse> categories = categoryService.getActiveCategories();
        
        return ResponseEntity.ok(
                ApiResponse.success(categories, "Categorías recuperadas exitosamente")
        );
    }
    
    /**
     * GET /api/v1/categories/{id}
     * 
     * Endpoint para obtener una categoría específica.
     * 
     * @param id el ID de la categoría
     * @return la categoría si existe, o 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(
                ApiResponse.success(category, "Categoría recuperada exitosamente")
        );
    }
    
    /**
     * POST /api/v1/categories
     * 
     * Endpoint para crear una nueva categoría.
     * 
     * @param request datos de la nueva categoría
     * @return la categoría creada
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @RequestBody CreateCategoryRequest request) {
        CategoryResponse createdCategory = categoryService.createCategory(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCategory, "Categoría creada exitosamente"));
    }
}
