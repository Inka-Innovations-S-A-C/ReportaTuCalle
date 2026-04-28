package com.reportatucalle.controller;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;

import com.reportatucalle.dto.CategoriaResponse;
import com.reportatucalle.service.CategoriaService;

class CategoriaControllerTest {

    @Test
    void listarCategoriasDevuelveOk() {
        CategoriaService service = mock(CategoriaService.class);
        CategoriaController controller = new CategoriaController(service);

        when(service.listar()).thenReturn(List.of(
                new CategoriaResponse(1L, "Baches", "Daños en el pavimento", "icon", "#FF0000")
        ));

        ResponseEntity<List<CategoriaResponse>> response = controller.listar();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        verify(service).listar();
    }

    @Test
    void obtenerCategoriaInexistenteDevuelveNotFound() {
        CategoriaService service = mock(CategoriaService.class);
        CategoriaController controller = new CategoriaController(service);

        when(service.obtener(999L)).thenThrow(new NoSuchElementException());

        ResponseEntity<CategoriaResponse> response = controller.obtener(999L);

        assertEquals(404, response.getStatusCode().value());

        verify(service).obtener(999L);
    }
}