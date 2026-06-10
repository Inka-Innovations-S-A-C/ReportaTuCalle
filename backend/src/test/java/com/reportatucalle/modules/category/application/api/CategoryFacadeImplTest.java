package com.reportatucalle.modules.category.application.api;

import com.reportatucalle.modules.category.domain.entity.AlgorithmType;
import com.reportatucalle.modules.category.domain.entity.Category;
import com.reportatucalle.modules.category.domain.repository.CategoryRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryFacadeImplTest {

    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final CategoryFacadeImpl facade = new CategoryFacadeImpl(categoryRepository);

    @Test
    void facadeMethods_returnValuesFromCategory() {
        Category category = Category.builder()
                .id(1L).name("Bache").description("Huecos").markerColor("#FF0000")
                .algorithmType(AlgorithmType.ROUTING).isActive(true).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertEquals(Optional.of(AlgorithmType.ROUTING), facade.getAlgorithmTypeForCategory(1L));
        assertEquals(Optional.of("Bache"), facade.getCategoryName(1L));
        assertTrue(facade.isCategoryActive(1L));
    }

    @Test
    void facadeMethods_handleMissingOrInactiveCategory() {
        Category inactive = Category.builder()
                .id(2L).name("Basura").description("Residuos").markerColor("#00FF00")
                .algorithmType(AlgorithmType.NONE).isActive(false).build();
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(inactive));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertFalse(facade.isCategoryActive(2L));
        assertEquals(Optional.empty(), facade.getCategoryName(99L));
        assertEquals(Optional.empty(), facade.getAlgorithmTypeForCategory(99L));
    }
}
