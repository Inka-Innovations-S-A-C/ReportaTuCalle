package com.reportatucalle.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;

import com.reportatucalle.dto.ReporteRequest;
import com.reportatucalle.dto.ReporteResponse;
import com.reportatucalle.service.ReporteService;

class ReporteControllerTest {

    private final String token = "token-provisional-valido-test@test.com";

    @Test
    void crearReporteDevuelveCreated() {
        ReporteService reporteService = mock(ReporteService.class);
        ReporteController controller = new ReporteController(reporteService);

        ReporteRequest request = new ReporteRequest();
        request.setTitulo("Bache");
        request.setDescripcion("Hay un bache grande");
        request.setLatitud(-12.05);
        request.setLongitud(-77.04);
        request.setCategoriaId(1L);

        ReporteResponse reporteResponse = new ReporteResponse();
        reporteResponse.setId(1L);
        reporteResponse.setTitulo("Bache");
        reporteResponse.setEstado("nuevo");

        when(reporteService.crear(request)).thenReturn(reporteResponse);

        ResponseEntity<ReporteResponse> response =
                controller.crear(token, request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Bache", response.getBody().getTitulo());

        verify(reporteService).crear(request);
    }

    @Test
    void listarSinTokenDevuelveUnauthorized() {
        ReporteService reporteService = mock(ReporteService.class);
        ReporteController controller = new ReporteController(reporteService);

        ResponseEntity<?> response = controller.listar(null);

        assertEquals(401, response.getStatusCode().value());
    }
}