package com.reportatucalle.modules.category.application.service;

import com.reportatucalle.modules.category.application.dto.CategoryResponse;
import com.reportatucalle.modules.category.application.dto.CreateCategoryRequest;
import com.reportatucalle.modules.category.application.dto.UpdateCategoryRequest;
import com.reportatucalle.modules.category.domain.entity.Category;
import com.reportatucalle.modules.category.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de Aplicación para Category.
 * 
 * Responsabilidades:
 * - Orquestar casos de uso de aplicación
 * - Traducir entre DTOs (API) y entidades de dominio
 * - Coordinar transacciones
 * 
 * Notas:
 * - Depende del puerto CategoryRepository (inyectado)
 * - NO depende de detalles de persistencia
 * - Los DTOs (CategoryResponse) son responsabilidad de la capa de aplicación
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Caso de Uso: Obtener todas las categorías activas.
     * 
     * Usado por el endpoint GET /api/v1/categories para llenar
     * el desplegable (Select) en el formulario de reportes.
     * 
     * Flujo:
     * 1. Consulta el repositorio (puerto) por categorías activas
     * 2. Transforma las entidades de dominio a DTOs para la respuesta
     * 3. Retorna la lista ordenada
     * 
     * @return lista de categorías activas como DTOs
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findAllActive()
                .stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getMarkerColor(),
                        category.getAlgorithmType()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Caso de Uso: Obtener una categoría específica por su ID.
     * 
     * @param categoryId el ID de la categoría
     * @return CategoryResponse si existe, o null si no
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getMarkerColor(),
                        category.getAlgorithmType()
                ))
                .orElse(null);
    }
    
    /**
     * Caso de Uso: Crear una nueva categoría.
     * 
     * Flujo:
     * 1. Valida que no exista una categoría con el mismo nombre
     * 2. Construye la entidad de dominio
     * 3. Persiste usando el repositorio (puerto)
     * 4. Transforma el resultado a DTO para respuesta
     * 
     * @param request datos de la nueva categoría
     * @return la categoría creada como DTO
     * @throws IllegalArgumentException si la categoría ya existe
     */
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        // Validación: no permitir duplicados
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + request.name());
        }
        
        // Construir la entidad de dominio
        Category newCategory = Category.builder()
                .name(request.name())
                .description(request.description())
                .markerColor(request.markerColor())
                .algorithmType(request.algorithmType())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // Persistir usando el puerto
        Category savedCategory = categoryRepository.save(newCategory);
        
        // Transformar a DTO
        return new CategoryResponse(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getDescription(),
                savedCategory.getMarkerColor(),
                savedCategory.getAlgorithmType()
        );
    }

    /**
     * Caso de Uso: Actualizar una categoría existente.
     */
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, UpdateCategoryRequest request) {
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        Category updatedCategory = Category.builder()
                .id(existingCategory.getId())
                .name(request.name() != null ? request.name() : existingCategory.getName())
                .description(request.description() != null ? request.description() : existingCategory.getDescription())
                .markerColor(request.markerColor() != null ? request.markerColor() : existingCategory.getMarkerColor())
                .algorithmType(request.algorithmType() != null ? request.algorithmType() : existingCategory.getAlgorithmType())
                .isActive(request.isActive() != null ? request.isActive() : existingCategory.getIsActive())
                .createdAt(existingCategory.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        Category savedCategory = categoryRepository.save(updatedCategory);

        return new CategoryResponse(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getDescription(),
                savedCategory.getMarkerColor(),
                savedCategory.getAlgorithmType()
        );
    }

    /**
     * Caso de Uso: Eliminar una categoría.
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.findById(categoryId).isPresent()) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }
        categoryRepository.deleteById(categoryId);
    }
}

