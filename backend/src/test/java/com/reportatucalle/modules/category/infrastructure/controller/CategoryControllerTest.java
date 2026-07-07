package com.reportatucalle.modules.category.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reportatucalle.modules.category.application.dto.CategoryResponse;
import com.reportatucalle.modules.category.application.dto.CreateCategoryRequest;
import com.reportatucalle.modules.category.application.dto.UpdateCategoryRequest;
import com.reportatucalle.modules.category.application.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.reportatucalle.shared.security.JwtAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationProvider;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetActiveCategories() throws Exception {
        CategoryResponse cat1 = new CategoryResponse(1L, "Cat1", "Desc", "Icon", null);
        CategoryResponse cat2 = new CategoryResponse(2L, "Cat2", "Desc", "Icon", null);
        when(categoryService.getActiveCategories()).thenReturn(Arrays.asList(cat1, cat2));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testGetCategoryById() throws Exception {
        CategoryResponse cat = new CategoryResponse(1L, "Cat1", "Desc", "Icon", null);
        when(categoryService.getCategoryById(1L)).thenReturn(cat);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testGetCategoryById_NotFound() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateCategory() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest("Cat1", "Desc", "Icon", null);
        
        CategoryResponse response = new CategoryResponse(1L, "Cat1", "Desc", "Icon", null);
        
        when(categoryService.createCategory(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testUpdateCategory() throws Exception {
        UpdateCategoryRequest request = new UpdateCategoryRequest(null, null, null, null, null);
        
        CategoryResponse response = new CategoryResponse(null, null, null, null, null);
        
        when(categoryService.updateCategory(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    public void testDeleteCategory() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isOk());
        
        verify(categoryService).deleteCategory(1L);
    }
}
