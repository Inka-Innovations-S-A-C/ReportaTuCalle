package com.reportatucalle.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.reportatucalle.dto.ReporteRequest;
import com.reportatucalle.dto.ReporteResponse;

class ReporteServiceTest {

    @Test
    void crearListarCambiarEstadoYEliminarReporte() {
        ReporteService service = new ReporteService();

        ReporteRequest request = new ReporteRequest();
        request.setTitulo("Bache");
        request.setDescripcion("Bache en la calle");
        request.setLatitud(-12.05);
        request.setLongitud(-77.04);
        request.setCategoriaId(1L);

        ReporteResponse creado = service.crear(request);

        assertNotNull(creado);
        assertEquals("Bache", creado.getTitulo());

        List<ReporteResponse> lista = service.listar();

        assertFalse(lista.isEmpty());

        ReporteResponse actualizado = service.cambiarEstado(creado.getId(), "revisado");

        assertEquals("revisado", actualizado.getEstado());

        service.eliminar(creado.getId());

        assertTrue(service.listar().isEmpty());
    }
}