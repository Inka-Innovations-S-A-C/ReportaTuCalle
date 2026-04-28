package com.reportatucalle.integration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.reportatucalle.dto.ReporteRequest;
import com.reportatucalle.dto.ReporteResponse;
import com.reportatucalle.service.ReporteService;

@SpringBootTest
class ReporteIntegrationTest {

    @Autowired
    private ReporteService reporteService;

    @Test
    void crudBasicoReporteFunciona() {
        ReporteRequest request = new ReporteRequest();
        request.setTitulo("Poste caído");
        request.setDescripcion("Hay un poste caído en la avenida");
        request.setLatitud(-12.05);
        request.setLongitud(-77.04);
        request.setCategoriaId(1L);

        ReporteResponse creado = reporteService.crear(request);

        assertNotNull(creado);
        assertEquals("Poste caído", creado.getTitulo());

        List<ReporteResponse> lista = reporteService.listar();

        assertFalse(lista.isEmpty());
        assertEquals("Poste caído", lista.get(0).getTitulo());

        ReporteResponse actualizado = reporteService.cambiarEstado(1L, "revisado");

        assertNotNull(actualizado);
        assertEquals("revisado", actualizado.getEstado());

        reporteService.eliminar(1L);

        List<ReporteResponse> listaFinal = reporteService.listar();

        assertTrue(listaFinal.isEmpty());
    }
}