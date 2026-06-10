package com.reportatucalle.modules.category.application.service;

import com.reportatucalle.modules.category.application.dto.CategoryResponse;
import com.reportatucalle.modules.category.application.dto.CreateCategoryRequest;
import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.category.domain.entity.Category;
import com.reportatucalle.modules.category.domain.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getActiveCategories_returnsMappedResponses() {
        Category bache = Category.builder()
                .id(1L).name("Bache").description("Huecos").markerColor("#FF0000")
                .algorithmType(AlgorithmType.ROUTING).isActive(true).build();
        Category basura = Category.builder()
                .id(2L).name("Basura").description("Residuos").markerColor("#00FF00")
                .algorithmType(AlgorithmType.NONE).isActive(true).build();
        when(categoryRepository.findAllActive()).thenReturn(List.of(bache, basura));

        List<CategoryResponse> result = categoryService.getActiveCategories();

        assertEquals(2, result.size());
        assertEquals("Bache", result.get(0).name());
        assertEquals("#00FF00", result.get(1).markerColor());
    }

    @Test
    void getCategoryById_whenExists_returnsResponse() {
        Category category = Category.builder()
                .id(7L).name("Alumbrado").description("Postes").markerColor("#FFFF00")
                .algorithmType(AlgorithmType.CONNECTIVITY).isActive(true).build();
        when(categoryRepository.findById(7L)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.getCategoryById(7L);

        assertNotNull(response);
        assertEquals(7L, response.id());
        assertEquals("Alumbrado", response.name());
    }

    @Test
    void getCategoryById_whenMissing_returnsNull() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(categoryService.getCategoryById(99L));
    }

    @Test
    void createCategory_whenNameIsNew_savesCategory() {
        CreateCategoryRequest request = new CreateCategoryRequest("Seguridad", "Robos", "#123456", AlgorithmType.NONE);
        when(categoryRepository.existsByName("Seguridad")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            return Category.builder()
                    .id(10L).name(c.getName()).description(c.getDescription()).markerColor(c.getMarkerColor())
                    .algorithmType(c.getAlgorithmType()).isActive(c.getIsActive())
                    .createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt()).build();
        });

        CategoryResponse response = categoryService.createCategory(request);

        assertEquals(10L, response.id());
        assertEquals("Seguridad", response.name());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_whenNameAlreadyExists_throwsException() {
        CreateCategoryRequest request = new CreateCategoryRequest("Bache", "Huecos", "#000000", AlgorithmType.ROUTING);
        when(categoryRepository.existsByName("Bache")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> categoryService.createCategory(request));

        assertTrue(ex.getMessage().contains("Ya existe"));
        verify(categoryRepository, never()).save(any());
    }
}
