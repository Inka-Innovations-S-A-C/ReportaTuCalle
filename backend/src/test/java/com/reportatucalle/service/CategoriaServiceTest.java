package com.reportatucalle.service;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.reportatucalle.dto.CategoriaResponse;

class CategoriaServiceTest {

    @Test
    void listarYObtenerCategoriaFunciona() {
        CategoriaService service = new CategoriaService();

        List<CategoriaResponse> categorias = service.listar();

        assertFalse(categorias.isEmpty());

        CategoriaResponse categoria = service.obtener(1L);

        assertEquals(1L, categoria.getId());
        assertEquals("Baches", categoria.getNombre());
    }

    @Test
    void obtenerCategoriaInexistenteLanzaError() {
        CategoriaService service = new CategoriaService();

        NoSuchElementException exception =
                assertThrows(
                        NoSuchElementException.class,
                        () -> service.obtener(999L)
                );

        assertNotNull(exception);
    }
}